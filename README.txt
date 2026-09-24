POKE AUTO CHESS ME v1.4.3 (J2ME - CLDC 1.0 / MIDP 2.0, offline 1 nguoi choi)
=====================================================================

CAU TRUC
  build.xml         Ant build (compile -> preverify -> jar + jad)
  build.properties  ghi de duong dan neu tu dong tim khong ra
  build-poke.bat    giong build-nro.bat: dat JAVA_HOME/KVEM_HOME roi chay "ant clean dist"
  src/pac/*.java    ma nguon game va cac module combat CLDC-safe
  res/sp/           386 sprite PNG (0.png .. 385.png), theo thu tu National Dex
  res/raw/          386 atlas PNG + DAT goc, nguon animation chinh
  res/av/           386 avatar rieng tu bo portrait chinh thuc
  tools/make_sprites.py  tao lai res/sp tu thu muc assets (can: pip install pillow)
  CREDITS.txt       nguon sprite
  docs/CHI_SO_VA_TRANG_THAI_POKEMON.md  bang chu viet tat, chi so va trang thai combat

PHIEN BAN
  v1.4.3: mua Legendary co song sang; IDLE goc; RMS chi luu dau round xep co.
  v1.4.2: hieu ung tien hoa 8 frame; pet cap 2 mau vang, cap 3 mau do.
  v1.4.1: hotfix hau tran; don projectile/skill/board FX va state tan cong,
          tiep tuc animation ket qua bang visual clock thay vi battle tick da dung.
  v1.4.0: Stable offline; dong bang gameplay/save v9, release gate Full/Lite,
          animation SPAWN goc va con tro silhouette + border vuong mong.
  v1.3.9RC2: con tro xep co om silhouette pet, khong bong duoi chan;
              pet moi mua chay 5 frame SPAWN goc tren hang cho.
  v1.3.9RC: dong bang gameplay; regression them save hong, deterministic battle
             va kiem tra truc tiep noi dung/manifest/JAD cua Full va Lite.
  v1.3.9b: gioi han them cache sprite fallback, avatar, avatar Gen 4-9 va skill
           rieng; giam dinh RAM khi cuon Collection va choi run dai.
  v1.3.9a: ban toi uu RAM dau tien; gioi han cache animation theo LRU va ghi nho
           asset thieu de Lite khong thu nap lai moi frame. MIDlet-Version 1.3.9.
  v1.3.7: Explore Hub, Farm Pet, Gacha, Bai Pokemon va Collection MAP 143 dungeon.
  v1.3.8: Ho so ca nhan, ten nguoi choi va Pokemon dai dien tu bo suu tap da mo khoa.
  v1.3.6: Audit ability/item; them passive source-backed Gen 1-3 va About chinh thuc.
  v1.3.5: Fixed-step combat tach render; noi suy muot va hien FPS thuc/target.
  v1.3.4: Du 25 status co tai lieu; them san trang About/Về game cho loi nhan cong dong.
  v1.3.3: Responsive UI Compact/Standard/Large cho man hinh 128x160 den landscape.
  v1.3.2: Collection phan loai Gen 1-9; them 558 Pokemon Gen 4-9 va portrait
          tu source goc. Full co 6 clip/8 huong; Lite dung portrait tinh.
          Gen 4-9 chi de xem, khong vao shop/AI/doi hinh/Battle.
  v1.3.1: 4 muc hieu nang hien ro 10/16/20/25 FPS cho may that; giam cap phat RAM
          trong UI Battle; co nut choi lai tu dau va lich su gon theo hang 9 avatar.
  v1.3.0: save/resume run bang RMS rieng; lich su 5 run gom doi hinh, 3 item/pet
          va bieu do thang/thua; build Full va Lite (bo raw/sfx nang).
  v1.2.9: economy tach base/lai/streak/thang/item; doi hinh PvE co dinh va boss
          Gyarados, Mewtwo-Mew, Tower Duo, Legendary Birds kem held item goc.
  v1.2.8: them behavior phu theo ten move; hoan thien 16 status, cleanse, hard-CC,
          charm danh dong minh, confusion tu gay sat thuong va Locked chan di chuyen.
  v1.2.7: them item passive dispatcher va cac trigger khoi tran/theo nhip/di chuyen/
          danh/nhan sat thuong/dung chieu/ha guc/tu tran; Collection hien noi tai.
  v1.2.6: port bang moc rieng va hieu ung chien dau cho du 31 he tu source goc;
          UI hien dung moc 2/3/4/5, 2/4/6/8, 3/5/7/9... theo tung he.
  v1.2.5: dong bo version; sua regression test theo luong ghep item thu cong;
          build chay regression suite truoc khi preverify/dong goi JAR.
  v1.2.4c: sua loi javac Java ME cu khong suy luan duoc PlayPathScreen/MenuScreen
          trong toan tu ba ngoi; doi sang if/else tuong thich CLDC.
  v1.2.4b: info shop chia avatar trai/noi dung 3 dong; Battle co hover xanh,
          tab Item 3 slot tron, tab He cuon + luoi 3x3; chon lo trinh mot lan trong RMS.
  v1.2.4a: info shop co avatar, gia `$1` viet lien; thanh sat thuong dich dao chieu;
          Battle Stats doi thanh DMG/HP/MP/He/2 o trong; tab He chia 1/3 + 2/3.
  v1.2.4: Viet hoa info duoi, icon tien hoa lon; ky hieu $ sat so vang;
          More co Ghep do/Tui vat pham/He cong huong; hang dock doi Item/He;
          hover He lam sang Pokemon va hien buff ben duoi; them 2 mode thu nghiem.
  v1.2.4-preview: Viet hoa info duoi, gian icon he, thay dong 3x bang icon tien hoa;
          sua bang He khi moi he chi co 1 Pokemon; them dai he kich hoat o mep ban;
          them mode 30 Vong - 9 Manh va mode The he 1 voi boss co dinh moi 5 vong.
  v1.2.3d: sua loi compile do StringBuffer trung ten tham so b trong itemBonusSummary.
  v1.2.3c: them nut Dong canh Ghep/Lam moi; Trai/Phai chuyen ca 3 nut;
           FIRE Dong quay lai xep doi, phim 0 van dong nhanh.
  v1.2.3b: Detail luc xep doi dong bo Battle Detail; Viet hoa chi so co dau hai cham;
           Trang bi o cuoi, 3 o tron va dong tong bonus vang nhat.
  v1.2.3a: cham tien hoa lon, ro mau; Battle Detail Viet hoa ten chi so co dau hai cham;
           Trang bi chuyen xuong cuoi, 3 o tron va dong tong bonus vang nhat; Trang thai mau do.
  v1.2.3: Battle Detail hien 3 trang bi va chuoi tien hoa; cham xanh/vang bao
          cap hien tai/cap con lai; bang He chon duoc va lam sang Pokemon dong gop;
          nut XP/Doi/More/He/GO, More gom khoa shop vong sau va Vat pham.
  v1.2.2c: khong cho chon trung vuot so luong that; FIRE lan nua tren mon het
           so luong se lay mon khoi o; khong hien ket qua gia khi thieu nguyen lieu.
  v1.2.2b: icon Ghep voi/Thanh tang len 16px; mo Ghep do luon xoa lua chon cu;
           them nut Lam moi canh nut Ghep de tra ca hai nguyen lieu ve trang thai chua chon.
  v1.2.2a: tab Cong thuc doi sang ma tran icon; mui ten dieu huong tab/ma tran;
           FIRE icon mo chi tiet va quay lai dung vi tri; manh ghep hien Ghep voi/Thanh.
  v1.2.2: nut Item mo man 2 tab Ghep do/Cong thuc; chon A+B, xem truoc va xac nhan;
          chi tiet item hien icon Ghep tu hoac cac mon Ghep ra theo ItemRecipe goc.
  v1.2.1b: pet dock dung the vuong nen trang/xam, avatar 22px; icon vang doi thanh $;
           FIRE pet mo Chi tiet/Mac vao; tam khoa ghep item cho luong Item tiep theo.
  v1.2.1a: item dung o tron toi, chi sang khi chon/cam; FIRE mo Trang bi/Chi tiet;
           khoi phuc bang thong tin Pokemon duoi cung; item tren san xep doc trong vong tron;
           the combat lon hon, tach dong item, gia tri HP/chi so va progress.
  v1.2.1: them kho trang bi co mui ten/thanh cuon va hang avatar Pokemon tren san;
          FIRE xem chi tiet, tu chon hai mon de ghep roi trang bi vao Pokemon;
          icon trang bi ro hon tren san va hien trong the thong ke combat.
  v1.2.0a: doi nut Ban thanh Item; Pokemon tren board/bench hien icon trang bi;
            giu ban bang cach keo Pokemon len shop hoac phim 7.
  v1.2.0: Item Core bam ItemStats/ItemRecipe app.zip; 3 o trang bi moi pet;
          phim # mo tui, FIRE trang bi/ghep; item theo pet khi di chuyen/tien hoa/ban.
  v1.1.9: FIRE tren item mo bang chi tiet day du; ket thuc combat giu animation
          victory den khi FIRE; bang Sat thuong tran dau them XP, pet con va vang.
  v1.1.8: sau moi tran hien bang sat thuong TA/ĐỊCH theo tung cap avatar;
          het run hien doi hinh cuoi, HP/vang, thuong va cong huong dang kich hoat.
  v1.1.7a: can bang khoang cach list/detail Collection; bo cau mo ta item chung chung;
           sua popup cong huong rong; nut Battle Stats hien Khiên va Chặn.
  v1.1.7: Collection > Vat pham co du 358 item tu app.zip; icon pixel nguoi dung cung cap;
          hien ten VI/EN, nhom, chi so goc va cong thuc ghep khi co.
  v1.1.6c: bitmap font giu dung mau UI goc; sua icon cong huong chen dong;
          danh dau moc dang kich hoat bang (2), (4), hoac (6).
  v1.1.6b: font tahoma_7b gan co NRO; dung truc tiep item_tps_pixel, scale cache luc chay.
  v1.1.6a: sua line-height bitmap font bi lay nham glyph SPACE lam UI chong chu.
  v1.1.6: font bitmap NRO 7px toan game; Collection co 10 item dau tien va icon PNG8 24px;
          MIDlet-Vendor doi thanh KhaiLy.
  v1.1.5a: bang cong huong bo ten he sau icon; font nho va them khoang cach noi dung.
  v1.1.5: pet KO hien HP 0 ngay; bang cong huong dung icon he va mo ta bonus 2/4/6.
  v1.1.4a: bo dong huong dan; nhan phe hien alive/total; avatar pet KO chuyen xam.
  v1.1.4: NHAN thanh KHIEN tao; mui ten dieu huong tab/lưoi; HP/MP dung dung ti le ca nhan.
  v1.1.3: them tong chi so duoi luoi; doi KHIEN thanh MP; bo icon he tren the shop.
  v1.1.2: bang 3x3 luon hien duoi ban co; nut phe + 6 nut chi so tren man hinh;
          thong ke DMG/nhan/chan/hoi/HP/khien truc tiep theo tung pet.
  v1.1.1: icon he trong bang tin; the shop mau theo rarity; combat inspector
          2 tab doi minh/doi dich, moi tab luoi 3x3 va trang chi tiet tung pet.
  v1.1.0: Viet hoa 31 he + 194 ten chieu Gen 1-3; font thong tin nho;
          thanh HP/PP co dinh 35px, mau theo doi va chia soc moi 25 HP.
  v1.0.9j: them man Cài đặt/Settings; chon ngon ngu tai day thay vi phim tat *.
  v1.0.9i: VI/EN (mac dinh VI, bam * o menu); bo res/anim va loader strip cu.
  v1.0.9h: o chon/HP/MP bam raw frame; Collection dung portrait 32px ro rang.
  v1.0.9g: raw pet tren menu/Explore; Collection thay DEBUG bang ITEMS placeholder.
  v1.0.9f: du 386 Pokemon Gen 1-3 dung raw spritesheet + frame index goc.
  v1.0.9e: render setup hai luot de o sau khong xoa canh/duoi pet o truoc;
            pet setup quay cheo xuong-trai (direction 7).
  v1.0.9d: center than pet trong o board/bench; combat van giu offset atlas goc.
  v1.0.9c: sua pet dang cam bi teo thanh cham; preview dung chung raw renderer.
  v1.0.9b test: toan bo Gen 1 dung raw spritesheet; truoc Auto Chess co mode
                NORMAL / UNLIMITED GOLD de roll, mua va test hieu nang.
  v1.0.9a demo: Pikachu/Mewtwo/Mew dung truc tiep spritesheet + frame data goc;
                cac Pokemon con lai giu renderer v1.0.9 de so sanh.
  v1.0.9: sprite dung canvas/neo chan goc, khong crop alpha tung animation;
          skill co frame timeline rieng, hien 12 tick va co cast-ring du phong.

CACH BUILD
  1. Giai nen vao vd D:\PokeAutoChessME
  2. Sua 2 dong JAVA_HOME / KVEM_HOME trong build-poke.bat neu can
  3. Chay build-poke.bat (tu dong chay regression suite truoc khi dong goi)
  4. Ket qua: build\dist\PokeAutoChess-Full.jar/.jad va PokeAutoChess-Lite.jar/.jad

  build.xml tu tim cldcapi*.jar, midpapi*.jar va preverify.exe ben trong KVEM_HOME.
  Neu bao "Could not find ...", bo comment cac dong tuong ung trong build.properties.

DIEU KHIEN
  Menu / Kham pha:  phim 2/4/6/8 hoac d-pad di chuyen, 5/FIRE = chon / bat
  Auto Chess:       d-pad di chuyen con tro, FIRE = mua / cam / dat
                    1 = Reroll (2g)   3 = Mua XP (4g)   7 = Ban   9 = Danh   * = Synergy
                    SOFT1 = Danh, SOFT2 / 0 = thoat (bam 2 lan)
  Trong tran:       FIRE doi toc do x1/x2/x4, 9 = bo qua ngay
                    trai/phai = chuyen Pokemon tren bang theo doi
  Collection:       FIRE mo bang chi tiet animation; trai/phai doi 8 huong,
                    len/xuong doi Idle/Attack/Victory, 1/3 doi status,
                    7/9 doi melee/range/hit effect, FIRE dong bang.
                    Ngoai danh sach: trai/phai doi tab POKEMON/TYPES/DEBUG.
  Cheat test:       tai menu chinh bam # de mo tat ca Pokemon va 9999 Ball
  Cam ung:          cham man hinh = d-pad ao

LUAT CHOI
  Che do 1: di tren ban do, dung canh Pokemon hoang, bam FIRE, canh thanh timing.
            Bat duoc -> mo khoa ca dong ho tien hoa (dung duoc o Auto Chess).
  Che do 2: run 20 vong. Mua 3 con giong nhau -> tien hoa. Synergy he 2/4/6 con.
            Boss o vong 8/12/16/20. Thang/ thua deu duoc thuong Poke Ball.

ROSTER GEN 1-3
  Co du 386 Pokemon tu Bulbasaur den Deoxys. Chi so, rarity va synergy lay tu
  file precomputed cua source app goc. Danh sach trong Data.java theo National Dex,
  vi vay 0=Bulbasaur, 149=Mewtwo, 385=Deoxys.
  Cac nhanh tien hoa khong duoc chon lam nhanh mac dinh van xuat hien trong shop
  nhu mot family rieng, dam bao cheat mode co the test du ca 386 con.

HIEU UNG TRAN DAU
  Ban nay noi suy chuyen dong giua cac o, co idle bob, lao toi khi danh can chien,
  dan bay cho don danh xa, vong niem chu cho ky nang, hit flash, crit/heal/miss/KO,
  HP/mana/shield va tang toc x1/x2/x4. Tat ca dung primitive MIDP de chay nhe.
  Ban 1.2 dung animation attack/status tu asset goc: Burn, Poison, Freeze, Sleep,
  Paralysis, Confusion, Charm, Wound, Blind, Armor Break, Silence va Protect.
  Ban 1.0.6 co 386 strip ky nang rieng (8 frame/Pokemon) lay tu ability asset goc.
  Smoke, Gas, Spikes, Web, Terrain, Ember va Firestarter de lai animation tren o san;
  mot Pokemon co the hien dong thoi toi da 3 overlay buff/debuff.
  Tu ban 1.0.7 icon trang thai khong ve tren dau Pokemon; ten trang thai hien
  trong bang theo doi ben duoi de san dau khong bi roi.

UI V1.0.7
  HUD dung icon goc cho HP, gold, Poke Ball/stat va du 31 icon he/synergy.
  Collection co 3 tab: danh sach Pokemon, thong tin he 2/4/6 va VFX debugger.
  Toc do di chuyen bam source goc: 500/(0.5+Speed/100) ms moi o.

COMBAT V1.1 - DONG BO SOURCE GOC
  Moi tick = 100ms. Toc do danh dung cong thuc source goc:
      cooldown = base / (0.4 + Speed * 0.007)
  Sat thuong vat ly dung Defense, skill dung Special Defense:
      damage = raw / (1 + 0.05 * defense)
  Danh thuong nhan 5 PP. Moi Pokemon giu dung Speed, Special Defense, Max PP va
  ten Ability tu pokemons-data.csv. Burn, Poison, Freeze, Sleep va Paralysis co
  thoi gian va hieu ung rieng. Xem docs/SOURCE_MAPPING.md de port tiep gameplay.

SPRITE
  So trong ten file la index National Dex tru 1 (0=Bulbasaur ... 385=Deoxys).
  Ban 1.0.5 giu ti le sourceSize cua atlas goc va tao cam giac tien hoa lon dan:
  tier 1 toi thieu 28px, tier 2=36px, tier 3/final=44px. Pokemon khong lo theo
  atlas co the lon toi 52px. Khung nho nhat 16px de khong mat chi tiet.
  Ban 1.0.8 dung mot alpha bounds chung cho 96 frame cua tung Pokemon, nen doi
  huong khong doi scale va Pokemon o cot ngoai cung khong bi cat boi mep man hinh.
  Thieu file -> game ve hinh bang code.
  Tao lai:  python tools/make_sprites.py D:\duong\dan\assets
            (them  --mode portrait  neu muon dung anh chan dung 40x40 nen dac)

ANIMATION GOC
  Game doc truc tiep res/raw/<id>.png + <id>.dat, giu frame/index atlas goc.
  res/anim strip cu da bo tu v1.0.9i. Khi raw loi, game fallback res/sp.
  Khong giai nen toan bo assets vao src; assets goc chi can khi tao lai raw data.
  Avatar UI dung portrait Normal chinh thuc:
    python tools/make_avatars.py D:\portraits.zip res\av

  Tao lai roster Java tu source app goc:
    python tools/generate_gen123_roster.py pokemons-data.csv pokemon.ts src\pac\Data.java

  Tao lai status va attack effect tu cac goi asset goc:
    python tools/make_combat_fx.py status{tps} attacks{tps} res\status res\attack

  Tao lai skill rieng cua 386 Pokemon tu ability asset goc:
    python tools/make_species_skill_sprites.py abilities{tps} pokemons-data.csv res\sfx
