package pac;

/** Read-only runtime roster export for the resource/evolution workbook. */
public final class RosterExport{
    private static String q(String s){if(s==null)s="";return "\""+s.replace('\\','/')+"\"";}
    public static void main(String[] args){
        if(args.length>0)try{System.setOut(new java.io.PrintStream(new java.io.FileOutputStream(args[0]),true,"UTF-8"));}catch(Exception e){throw new RuntimeException(e.toString());}
        System.out.println("[");
        int n=0;
        for(int sp=0;sp<Data.N;sp++){
            if(n++>0)System.out.println(",");
            String route="";int next=EvolutionBranchData.defaultNext(sp);
            if(next>=0){int count=EvolutionBranchData.count(next);for(int c=0;c<count;c++){int s=EvolutionBranchData.choiceSpecies(next,c),form=EvolutionBranchData.choiceForm(next,c);if(c>0)route+=" / ";route+=form>0?SpecialFormData.name(form):Data.name[s];}}
            else if(EvolutionBranchData.purchaseChoice(sp))route="PURCHASE_CHOICE";
            int form=EvolutionVariantData.formFor(sp);if(form==0)form=SpecialFormData.memoryForm(sp);
            System.out.print("{\"sp\":"+sp+",\"dex\":"+Data.nationalDex(sp)+",\"name\":"+q(Data.name[sp])+",\"gen\":"+Data.generation(sp)+",\"base\":"+Data.isBase(sp)+",\"tier\":"+Data.tier[sp]+",\"family\":"+q(Data.name[Data.fam[sp]])+",\"route\":"+q(route)+",\"mega\":"+MegaData.available(sp)+",\"form\":"+form+",\"formName\":"+q(form>0?SpecialFormData.name(form):"")+",\"formKey\":"+q(form>0?SpecialFormData.key(form):"")+"}");
        }
        System.out.println("\n]");
    }
}
