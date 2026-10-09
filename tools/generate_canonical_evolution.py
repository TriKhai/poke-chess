"""Generate append-safe National Dex evolution topology from cached PokeAPI.

Regional-only children remain in their family, but are never normal default
evolutions. Conditions (stones, friendship, gender) are intentionally adapted
to the game's three-copy choice mechanic.
"""
import csv
import json
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
REF = ROOT / 'outputs/all-gen-audit/reference'
# These require a specific regional parent, not the ordinary species.
REGIONAL_ONLY = {862, 864, 865, 866, 867, 902, 903, 904, 980}

def main():
    rows = list(csv.DictReader((REF / 'pokemon_species.csv').open(encoding='utf-8')))
    prev = {int(r['id']): int(r['evolves_from_species_id'] or 0) for r in rows if int(r['id']) <= 1025}
    edges = [(p, d) for d, p in sorted(prev.items()) if p and d not in REGIONAL_ONLY]
    def arr(values):
        return ','.join(map(str, values))
    source = '''package pac;

/** Generated canonical topology; species indices and save IDs are not changed. */
public final class CanonicalEvolutionData {
    private CanonicalEvolutionData(){}
    private static final int[] PARENT={%s};
    private static final int[] CHILD={%s};
    private static final int[] FAMILY_PARENT={%s};
    private static final int[] FAMILY_CHILD={%s};
    private static final int[] BRANCH=new int[Data.MAX];
    private static int root(int dex){int p=parent(dex);return p==0?dex:root(p);}
    private static int parent(int dex){for(int i=0;i<FAMILY_CHILD.length;i++)if(FAMILY_CHILD[i]==dex)return FAMILY_PARENT[i];return 0;}
    private static int depth(int dex){int p=parent(dex);return p==0?1:depth(p)+1;}
    public static void init(){
        for(int s=0;s<Data.N;s++){
            int dex=Data.nationalDex(s),base=Data.speciesForDex(root(dex));
            Data.evo[s]=-1;Data.fam[s]=base>=0?base:s;Data.tier[s]=depth(dex);
            if(base>=0&&base!=s){Data.cost[s]=Data.cost[base];Data.zones[s]=0;}
        }
        for(int i=0;i<CHILD.length;i++){int p=Data.speciesForDex(PARENT[i]),s=Data.speciesForDex(CHILD[i]);if(p>=0&&s>=0&&Data.evo[p]<0)Data.evo[p]=s;}
        for(int s=0;s<Data.N;s++)BRANCH[s]=calculateBranch(s);
    }
    /** Options are attached to the ordinary result displayed by the merge. */
    public static int[] branches(int sp){
        int dex=Data.nationalDex(sp),p=parent(dex),first=0,n=0;
        for(int i=0;i<CHILD.length;i++)if(PARENT[i]==p&&Data.speciesForDex(CHILD[i])>=0){if(first==0)first=CHILD[i];n++;}
        if(n<2||dex!=first)return null;
        int[] out=new int[n];n=0;for(int i=0;i<CHILD.length;i++)if(PARENT[i]==p&&Data.speciesForDex(CHILD[i])>=0)out[n++]=CHILD[i];return out;
    }
    public static int synergyBranch(int sp){return sp>=0&&sp<Data.N?BRANCH[sp]:0;}
    public static boolean ancestor(int sp,int descendant){int dex=Data.nationalDex(descendant),want=Data.nationalDex(sp);while(parent(dex)!=0){dex=parent(dex);if(dex==want)return true;}return false;}
    private static int calculateBranch(int sp){
        int dex=Data.nationalDex(sp);
        while(parent(dex)!=0){int p=parent(dex),first=0,n=0;for(int i=0;i<CHILD.length;i++)if(PARENT[i]==p&&Data.speciesForDex(CHILD[i])>=0){if(first==0)first=CHILD[i];n++;}if(n>1&&dex!=first)return 10000+dex;dex=p;}
        return 0;
    }
}
''' % (arr(p for p,d in edges), arr(d for p,d in edges), arr(p for d,p in sorted(prev.items()) if p), arr(d for d,p in sorted(prev.items()) if p))
    (ROOT / 'src/pac/CanonicalEvolutionData.java').write_text(source, encoding='utf-8')
    (ROOT / 'outputs/all-gen-audit/canonical-edges.json').write_text(json.dumps([dict(parent=p,child=d) for p,d in edges], indent=2), encoding='utf-8')
    print('Generated',len(edges),'ordinary edges;',len(REGIONAL_ONLY),'regional-only children excluded from defaults')

if __name__ == '__main__':
    main()
