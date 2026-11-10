package com.example.data

data class TissueProperties(
    val id: String,
    val name: String,
    val t1Ms: Float,
    val t2Ms: Float,
    val protonDensity: Float,
    val description: String,
    val clinicalNote: String
)

data class MriNode(
    val code: String,
    val level: Int, // 1 = T1 (Dasar), 2 = T2 (Menengah), 3 = T3 (Lanjut)
    val title: String,
    val summaryShort: String,
    val detailFull: String,
    val analogi: String,
    val labType: String? = null // e.g. "larmor", "contrast", "presesi", "sar"
)

data class MriModule(
    val id: String,
    val code: String,
    val title: String,
    val subtitle: String,
    val description: String,
    val levelBadge: String,
    val iconName: String,
    val nodes: List<MriNode>
)

data class GlossaryItem(
    val term: String,
    val symbolOrAbbr: String,
    val moduleId: String,
    val nodeCode: String,
    val definition: String
)

data class StudyNote(
    val id: String,
    val title: String,
    val content: String,
    val category: String, // "Protokol", "Fisika", "Artefak", "Keselamatan", "Catatan Bebas"
    val dateString: String
)

data class QuizQuestion(
    val id: String,
    val moduleId: String,
    val nodeCode: String,
    val question: String,
    val options: List<String>,
    val correctIndex: Int,
    val explanation: String
)

object MriRepository {

    // Konstanta Giromagnetik (MHz / Tesla)
    const val GAMMA_1H = 42.58f
    const val GAMMA_13C = 10.71f
    const val GAMMA_19F = 40.05f
    const val GAMMA_23NA = 11.26f

    val tissues = listOf(
        TissueProperties(
            id = "fat",
            name = "Adiposa / Lemak (Fat)",
            t1Ms = 260f,
            t2Ms = 80f,
            protonDensity = 0.90f,
            description = "T1 sangat pendek karena gerak molekul lipid dekat dengan frekuensi Larmor. T2 sedang.",
            clinicalNote = "Tampak hiperintens (sangat terang) pada T1W dan intermediet pada T2W."
        ),
        TissueProperties(
            id = "wm",
            name = "Substansia Alba (White Matter)",
            t1Ms = 780f,
            t2Ms = 90f,
            protonDensity = 0.70f,
            description = "Kaya lipid mielin aksonal, waktu relaksasi T1 lebih cepat dibanding Substansia Grisea.",
            clinicalNote = "Tampak lebih terang dari korteks otak pada T1W, lebih gelap pada T2W."
        ),
        TissueProperties(
            id = "gm",
            name = "Substansia Grisea (Gray Matter)",
            t1Ms = 920f,
            t2Ms = 100f,
            protonDensity = 0.82f,
            description = "Kandungan air bebas lebih tinggi dibanding Substansia Alba.",
            clinicalNote = "Memberikan kontras anatomis diferensial yang jelas pada otak."
        ),
        TissueProperties(
            id = "csf",
            name = "Cairan Serebrospinal (CSF)",
            t1Ms = 4000f,
            t2Ms = 2000f,
            protonDensity = 1.0f,
            description = "Cairan bebas (air murni fisiologis). T1 dan T2 sangat panjang.",
            clinicalNote = "Hipointens (gelap) pada T1W, sangat hiperintens (terang benderang) pada T2W."
        ),
        TissueProperties(
            id = "muscle",
            name = "Otot Skelet (Muscle)",
            t1Ms = 870f,
            t2Ms = 45f,
            protonDensity = 0.75f,
            description = "T2 sangat pendek karena ikatan hidrasi protein miofibril yang rapat.",
            clinicalNote = "Tampak gelap / intermediet pada sekuens T2 standar."
        ),
        TissueProperties(
            id = "edema",
            name = "Edema Patologis / Lesi",
            t1Ms = 2200f,
            t2Ms = 300f,
            protonDensity = 0.95f,
            description = "Penumpukan air interstitial akibat inflamasi, iskemia akut, atau tumor.",
            clinicalNote = "Sangat menonjol (hiperintens) pada pembobotan T2w dan sekuens STIR."
        )
    )

    val modules = listOf(
        MriModule(
            id = "M01",
            code = "M01",
            title = "Fisika Dasar MRI & Presesi Spin",
            subtitle = "Atom Hidrogen, Vektor Magnetisasi Netto, dan Larmor",
            description = "Memahami mengapa proton hidrogen dipilih, distribusi Boltzmann, frekuensi Larmor f = γ·B0, dan pembentukan sinyal induksi bebas (FID).",
            levelBadge = "T1 - T3 Dasar",
            iconName = "atom",
            nodes = listOf(
                MriNode(
                    code = "M01.T1.1",
                    level = 1,
                    title = "Peran MRI Klinis & Proton Hidrogen (¹H)",
                    summaryShort = "Atom Hidrogen (¹H) memiliki proton ganjil tunggal yang menghasilkan momentum magnetik bersih dan melimpah dalam tubuh manusia (air dan lemak).",
                    detailFull = "Inti atom dengan nomor massa ganjil memiliki sifat spin kuantum intrinsik. Tubuh manusia tersusun atas ~65-70% air (H₂O) dan molekul lipid, menjadikan proton hidrogen sumber sinyal radiofrekuensi paling kuat dalam pencitraan medis tanpa radiasi pengion.",
                    analogi = "Setiap proton seperti jarum kompas mikro yang berorientasi acak ketika tidak ada medan magnet luar.",
                    labType = "presesi"
                ),
                MriNode(
                    code = "M01.T1.5",
                    level = 1,
                    title = "Presesi dan Persamaan Frekuensi Larmor",
                    summaryShort = "Dalam medan B0, spin berputar presesi dengan frekuensi yang berbanding lurus: f₀ = γ · B₀. Untuk ¹H, γ = 42.58 MHz/Tesla.",
                    detailFull = "Pengaruh medan magnet eksternal B0 memaksa sumbu rotasi spin mengalami presesi. Pada scanner 1.5 Tesla, frekuensi Larmor hidrogen adalah ~63.87 MHz (rentang gelombang radio / VHF). Pada 3.0 Tesla, frekuensinya menjadi ~127.74 MHz.",
                    analogi = "Seperti gasing mainan yang miring dan berputar mengitari tarikan gravitasi bumi.",
                    labType = "larmor"
                ),
                MriNode(
                    code = "M01.T2.1",
                    level = 2,
                    title = "Vektor Magnetisasi Netto (NMV / M₀)",
                    summaryShort = "Sedikit kelebihan spin pada keadaan energi rendah sejajar paralel dengan B0 menghasilkan Net Magnetization Vector makroskopik sepanjang sumbu longitudinal (z).",
                    detailFull = "Berdasarkan Distribusi Boltzmann, fraksi spin paralel sedikit lebih banyak daripada antiparalel (hanya ~3-9 spin per satu juta). Namun jumlah triliunan proton menghasilkan magnetisasi makroskopik M0 yang cukup untuk diinduksi menjadi sinyal listrik pada koil penerima.",
                    analogi = "Kerumunan pendukung di stadion: selisih jumlah kursi yang ditempati menentukan gemuruh riuh total stadion.",
                    labType = "presesi"
                ),
                MriNode(
                    code = "M01.T2.3",
                    level = 2,
                    title = "Eksitasi RF, Nutasi 90°, dan FID",
                    summaryShort = "Pulsa radiofrekuensi (RF) pada frekuensi resonansi membalikkan NMV ke bidang transversal (xy). Penghentian pulsa menghasilkan sinyal Free Induction Decay (FID).",
                    detailFull = "Hukum Induksi Faraday menyatakan bahwa perubahan medan magnet transversal yang berputar akan menginduksi arus bolak-balik pada koil penerima. Segera setelah pulsa 90° mati, koherensi fase meluruh dan sinyal FID mengecil secara eksponensial.",
                    analogi = "Seperti memetik senar gitar akustik: getaran keras pertama kali terdengar lalu perlahan menghilang.",
                    labType = null
                )
            )
        ),
        MriModule(
            id = "M02",
            code = "M02",
            title = "Relaksasi T1/T2 & Pembobotan Kontras",
            subtitle = "Pemulihan Longitudinal, Peluruhan Transversal, dan Kontras",
            description = "Menguasai perbedaan intrinsik jaringan, parameter ekstrinsik TR dan TE, serta klasifikasi citra T1-Weighted, T2-Weighted, dan Proton Density (PDW).",
            levelBadge = "T1 - T2 Menengah",
            iconName = "contrast",
            nodes = listOf(
                MriNode(
                    code = "M02.T1.2",
                    level = 1,
                    title = "Dua Proses Relaksasi Simultan: T1 vs T2",
                    summaryShort = "T1 adalah waktu pemulihan magnetisasi longitudinal (spin-kisi ~63%), sedangkan T2 adalah waktu peluruhan koherensi transversal (spin-spin ~37%).",
                    detailFull = "Relaksasi T1 melibatkan transfer energi termal dari spin ke lingkungan kisi molekuler (lattice). Relaksasi T2 murni terjadi akibat fluktuasi medan magnet lokal antar molekul tetangga yang menghilangkan keseragaman fase presesi tanpa kehilangan energi ke kisi.",
                    analogi = "T1 seperti baterai yang diisi ulang secara bertahap; T2 seperti sekelompok pelari maraton yang awalnya start bersama lalu berpencar kecepatannya.",
                    labType = "contrast"
                ),
                MriNode(
                    code = "M02.T1.4",
                    level = 1,
                    title = "Pembobotan Citra: T1w, T2w, dan PDw",
                    summaryShort = "T1w (TR pendek, TE pendek), T2w (TR panjang, TE panjang), PDw (TR panjang, TE pendek). Nilai TR mengontrol T1; nilai TE mengontrol T2.",
                    detailFull = "Formula sinyal Spin Echo: S = PD · (1 - e^(-TR/T1)) · e^(-TE/T2). Dengan TR pendek (~400-600ms), jaringan belum pulih penuh sehingga selisih T1 tampak tajam. Dengan TE panjang (~80-120ms), selisih peluruhan T2 mendominasi kontras.",
                    analogi = "TR dan TE seperti dua kenop pengatur kontras foto: satu mengatur eksposur pemulihan, satu mengatur peluruhan bayangan.",
                    labType = "contrast"
                ),
                MriNode(
                    code = "M02.T2.2",
                    level = 2,
                    title = "Dephasing, T2*, dan Pulsa Refokusing 180°",
                    summaryShort = "Ketidakhomogenan medan magnet B0 mempercepat peluruhan sinyal menjadi T2*. Pulsa RF 180° membalikkan spin untuk membentuk Spin Echo sejati.",
                    detailFull = "T2* menggabungkan dephasing T2 alami dan ketidakhomogenan magnetik statis (ΔB0). Pulsa refokusing 180° membalikkan posisi fase spin sehingga spin yang berpresesi cepat dan lambat bertemu kembali sefase pada waktu TE.",
                    analogi = "Seperti pelari cepat dan lambat yang disuruh berbalik arah tepat di tengah lintasan: keduanya akan mencapai garis start kembali di saat yang bersamaan.",
                    labType = "contrast"
                )
            )
        ),
        MriModule(
            id = "M03A",
            code = "M03A",
            title = "Sekuens Spin Echo & Inversion Recovery",
            subtitle = "Konvensional SE, Turbo Spin Echo (TSE/FSE), STIR, dan FLAIR",
            description = "Prinsip pulsa inversi 180°, pemilihan TI (Inversion Time) untuk supresi lemak (STIR) dan CSF (FLAIR), serta akselerasi Turbo Factor (ETL).",
            levelBadge = "T1 - T3",
            iconName = "pulse",
            nodes = listOf(
                MriNode(
                    code = "M03A.T1.1",
                    level = 1,
                    title = "Sekuens Spin Echo (SE) Standar",
                    summaryShort = "Satu pulsa eksitasi 90° diikuti satu pulsa refokusing 180° pada interval TE/2 untuk membentuk echo di waktu TE.",
                    detailFull = "SE adalah sekuens baku emas (gold standard) untuk resolusi kontras jaringan karena menghilangkan artefak suseptibilitas magnetik statis. Namun, waktu pemindaian relatif panjang karena hanya merekam satu baris ruang-k per TR.",
                    analogi = "Satu kali ayunan bandul, satu kali pantulan gema suara.",
                    labType = null
                ),
                MriNode(
                    code = "M03A.T1.3",
                    level = 1,
                    title = "Inversion Recovery: Penekanan Sinyal (STIR & FLAIR)",
                    summaryShort = "Pulsa awal 180° membalikkan magnetisasi. Saat jaringan melewati titik nol (null point), pulsa eksitasi 90° dikirim sehingga sinyal jaringan tersebut padam.",
                    detailFull = "Formula titik nol: TI ≈ 0.693 · T1. STIR menggunakan TI pendek (~150-180 ms pada 1.5 T) untuk memadamkan lemak, ideal untuk visualisasi edema tulang. FLAIR menggunakan TI panjang (~2000-2500 ms) untuk memadamkan CSF, menonjolkan plak sklerosis multipel atau perdarahan subaraknoid.",
                    analogi = "Menembak foto tepat di saat ayunan pendulum melintasi titik tengah nol kecepatan.",
                    labType = null
                )
            )
        ),
        MriModule(
            id = "M04A",
            code = "M04A",
            title = "Pengodean Spasial: Gradien & Irisan",
            subtitle = "Gradien Slice Select, Frequency Encoding, dan Phase Encoding",
            description = "Bagaimana pemindai MRI memberikan alamat spasial 3D ke tiap sinyal: pemilihan bidang aksial/sagital/koronal, bandwidth, dan medan pandang (FoV).",
            levelBadge = "T1 - T2 Dasar",
            iconName = "grid",
            nodes = listOf(
                MriNode(
                    code = "M04A.T1.2",
                    level = 1,
                    title = "Mekanisme Koil Gradien Linier",
                    summaryShort = "Koil gradien menambahkan variasi medan magnet linier terhadap posisi spasial sehingga frekuensi presesi spin bervariasi sepanjang sumbu X, Y, atau Z.",
                    detailFull = "Dengan mengaktifkan medan gradien G, persamaan Larmor menjadi f(r) = γ · (B0 + G · r). Hal ini memungkinkan pemilahan sinyal berdasarkan frekuensi dan fase yang unik untuk tiap voxel.",
                    analogi = "Seperti tangga nada piano: tuts sebelah kiri bernada rendah, tuts sebelah kanan bernada tinggi.",
                    labType = null
                ),
                MriNode(
                    code = "M04A.T1.5",
                    level = 1,
                    title = "Pemilihan Irisan (Slice Selection) & Ketebalan",
                    summaryShort = "Ketebalan irisan ditentukan oleh rasio Transmit Bandwidth (BW) terhadap kekuatan Gradien Slice (Gss): Thickness = BW / (γ · Gss).",
                    detailFull = "Gradien Z untuk aksial, Gradien Y untuk koronal, dan Gradien X untuk sagital. Semakin curam gradien atau semakin sempit transmit bandwidth RF, semakin tipis irisan anatomis yang diperoleh.",
                    analogi = "Mengiris kue lapis dengan pisau yang ketebalannya diatur oleh jarak dua bilah paralel.",
                    labType = null
                )
            )
        ),
        MriModule(
            id = "M04B",
            code = "M04B",
            title = "Ruang-k (k-Space) & Rekonstruksi FFT",
            subtitle = "Matriks Data Mentah, Frekuensi Nyquist, dan Rekonstruksi Citra",
            description = "Memahami matriks domain spasial frekuensi k-Space, pusat kontras vs tepi resolusi detail, Teorema Sampling Nyquist, dan Transformasi Fourier Cepat.",
            levelBadge = "T2 Menengah",
            iconName = "chart",
            nodes = listOf(
                MriNode(
                    code = "M04B.T2.1",
                    level = 2,
                    title = "Arsitektur Matriks Ruang-k (k-Space)",
                    summaryShort = "Ruang-k adalah matriks data spasial frekuensi digital mentah sebelum direkonstruksi menjadi citra anatomi oleh Fast Fourier Transform (FFT).",
                    detailFull = "Sumbu horizontal ruang-k mewakili pengodean frekuensi (k_x), sedangkan sumbu vertikal mewakili pengodean fase (k_y). Setiap echo mengisi satu baris ruang-k per repetisi waktu (TR). Ruang-k bukan representasi citra langsung titik per titik.",
                    analogi = "Seperti lemari arsip dengan ratusan laci bernomor baris dan kolom yang menyimpan lembaran data mentah.",
                    labType = null
                ),
                MriNode(
                    code = "M04B.T2.10",
                    level = 2,
                    title = "Pusat Ruang-k (Kontras) vs Tepi (Resolusi Detail)",
                    summaryShort = "Baris tengah ruang-k (frekuensi spasial rendah) mengandung amplitudo sinyal tinggi dan kontras jaringan. Bagian tepi mengandung detail tepi dan ketajaman spasial.",
                    detailFull = "Jika pusat ruang-k ditutup filter, citra kehilangan kontras dan tampak seperti sketsa garis tepi (edge-detection). Jika bagian tepi ditutup, citra menjadi kabur (blur) namun kontras jaringan tetap utuh.",
                    analogi = "Pusat ruang-k adalah warna cat dasar lukisan; tepi ruang-k adalah garis kontur pensil penegas detail.",
                    labType = null
                )
            )
        ),
        MriModule(
            id = "M07",
            code = "M07",
            title = "Keselamatan MR (MR Safety Standards)",
            subtitle = "Zona ACR, Bahaya Efek Proyektil, SAR & Standar ASTM Implan",
            description = "Protokol ketat keselamatan radiografer dan pasien: zona I-IV, gaya translasi feromagnetik, batas pemanasan RF (SAR), dan label implan ASTM.",
            levelBadge = "T1 - T3 Wajib",
            iconName = "shield",
            nodes = listOf(
                MriNode(
                    code = "M07.T1.1",
                    level = 1,
                    title = "Empat Zona Keselamatan Fasilitas MRI (ACR)",
                    summaryShort = "Zona I: Area umum publik. Zona II: Area pendaftaran & skrining. Zona III: Area terkontrol petugas MR. Zona IV: Ruang magnetik scanner scanner berisiko tinggi.",
                    detailFull = "Pedoman American College of Radiology (ACR) mewajibkan akses ketat. Benda feromagnetik dilarang keras melintasi batas Zona III ke Zona IV tanpa pengawasan personel Level 2 bersertifikasi.",
                    analogi = "Bagaikan pos pengamanan bandara udara: dari lobi tiket, gerbang periksa, hingga ke dalam kabin pesawat.",
                    labType = "sar"
                ),
                MriNode(
                    code = "M07.T2.1",
                    level = 2,
                    title = "Medan Statis B0, Gaya Translasi & Efek Proyektil",
                    summaryShort = "Medan magnet statis selalu aktif 24/7 (bahkan saat scanner mati). Benda logam feromagnetik dapat ditarik dengan kecepatan tinggi menjadi peluru berbahaya.",
                    detailFull = "Gaya tarikan meningkat drastis seiring kedekatan dengan bore magnet (Spatial Gradient Field). Tabung gas oksigen, kursi roda, atau gunting dapat berubah menjadi proyektil mematikan dalam hitungan milidetik.",
                    analogi = "Gravitasi lubang hitam yang menyedot benda feromagnetik tanpa peringatan.",
                    labType = null
                ),
                MriNode(
                    code = "M07.T2.2",
                    level = 2,
                    title = "Specific Absorption Rate (SAR) & Pemanasan RF",
                    summaryShort = "SAR mengukur laju energi daya RF yang diserap jaringan tubuh (Watt/kg). Batas normal mode menurut IEC 60601-2-33 adalah 2.0 W/kg (Whole Body).",
                    detailFull = "Pulsa eksitasi dan refokusing 180° yang berulang pada sekuens TSE mentransfer energi termal ke tubuh pasien. Kenaikan suhu tubuh inti tidak boleh melampaui 1°C dalam kondisi normal.",
                    analogi = "Bagaikan microwave berkekuatan rendah yang menghangatkan secangkir air secara bertahap.",
                    labType = "sar"
                )
            )
        ),
        MriModule(
            id = "M08",
            code = "M08",
            title = "Artefak Citra MRI & Penanganannya",
            subtitle = "Aliasing, Motion Ghosting, Chemical Shift, dan Magic Angle",
            description = "Mengenali asal-usul artefak pengodean, pengaruh gerak fisiologis, artefak pergeseran kimia lemak-air, dan teknik koreksi klinis.",
            levelBadge = "T1 - T3 Klinis",
            iconName = "bug",
            nodes = listOf(
                MriNode(
                    code = "M08.T1.2",
                    level = 1,
                    title = "Artefak Aliasing (Wrap-around / Fold-over)",
                    summaryShort = "Terjadi saat anatomi pasien melampaui Field of View (FoV) sehingga sinyal terlipat ke sisi berlawanan citra akibat pelanggaran frekuensi Nyquist.",
                    detailFull = "Solusi klinis: memperbesar FoV, mengaktifkan No Phase Wrap / Phase Oversampling, atau menukar arah frekuensi dan pengodean fase.",
                    analogi = "Foto panorama yang tepian kanannya terpotong lalu menumpuk di sisi kiri bingkai.",
                    labType = null
                ),
                MriNode(
                    code = "M08.T1.4",
                    level = 1,
                    title = "Artefak Pergeseran Kimia (Chemical Shift)",
                    summaryShort = "Perbedaan frekuensi presesi antara proton lemak dan air (~3.5 ppm atau ~220 Hz pada 1.5 T) menghasilkan pita terang dan gelap di perbatasan organ.",
                    detailFull = "Karena gradien pengodean frekuensi mengasumsikan frekuensi murni menunjukkan posisi, pergeseran frekuensi kimia lemak menyebabkan posisinya tergeser beberapa piksel sepanjang sumbu baca.",
                    analogi = "Seperti bayangan timbul (bas-relief) di salah satu sisi batas ginjal dan lemak retroperitoneal.",
                    labType = null
                )
            )
        ),
        MriModule(
            id = "M09",
            code = "M09",
            title = "Optimasi Protokol & Kompromi Parameter",
            subtitle = "Persamaan Waktu Akuisisi (TA), SNR, Resolusi Spasial & Matriks",
            description = "Menguasai segitiga kompromi radiografi MRI: meningkatkan Signal-to-Noise Ratio (SNR), memangkas Scan Time (TA), dan menjaga resolusi detail anatomis.",
            levelBadge = "T1 - T3 Praktik",
            iconName = "settings",
            nodes = listOf(
                MriNode(
                    code = "M09.T1.2",
                    level = 1,
                    title = "Formula Waktu Akuisisi Citra (TA)",
                    summaryShort = "TA 2D konvensional: TA = (TR · N_fase · NEX) / ETL. Matriks frekuensi tidak memengaruhi durasi waktu pemindaian.",
                    detailFull = "Pada sekuens TSE, pembagian dengan Echo Train Length (ETL / Turbo Factor) memotong waktu akuisisi secara signifikan. Menambah NEX (Number of Excitations) meningkatkan SNR sebanding √NEX namun melipatgandakan TA.",
                    analogi = "Seperti waktu memasak kue: lama oven dikali jumlah loyang dibagi jumlah rak bertingkat.",
                    labType = null
                ),
                MriNode(
                    code = "M09.T2.1",
                    level = 2,
                    title = "Segitiga Kompromi: SNR, Resolusi, dan Durasi",
                    summaryShort = "SNR berbanding lurus dengan Volume Voxel (V = Δx · Δy · Slice Thickness) dan √(NEX / Receive Bandwidth).",
                    detailFull = "Memperkecil voxel memberikan resolusi spasial lebih tajam tetapi menurunkan SNR secara drastis. Radiografer harus menyeimbangkan kebutuhan diagnostik dan keterbatasan waktu toleransi pasien.",
                    analogi = "Bagaikan menarik selimut sempit: jika kepala ditutup rapat, kaki akan terbuka.",
                    labType = null
                )
            )
        )
    )

    val glossary = listOf(
        GlossaryItem("Larmor (Frekuensi)", "f₀", "M01", "M01.T1.5", "Frekuensi presesi putaran spin proton hidrogen di bawah pengaruh kuat medan magnet eksternal B0 (f₀ = γ · B₀)."),
        GlossaryItem("TR (Repetition Time)", "TR", "M02", "M02.T1.4", "Waktu interval antar pulsa eksitasi RF berturut-turut pada irisan yang sama (satuan milidetik ms). Mengontrol pembobotan kontras T1."),
        GlossaryItem("TE (Echo Time)", "TE", "M02", "M02.T1.4", "Waktu dari pemberian pulsa RF eksitasi 90° hingga puncak sinyal echo dicatat (satuan ms). Mengontrol pembobotan kontras T2."),
        GlossaryItem("TI (Inversion Time)", "TI", "M03A", "M03A.T1.3", "Waktu jeda antara pulsa inversi awal 180° dengan pulsa eksitasi 90° pada sekuens Inversion Recovery (STIR/FLAIR)."),
        GlossaryItem("NMV (Net Magnetization Vector)", "M₀", "M01", "M01.T2.1", "Vektor makroskopik total yang dihasilkan oleh akumulasi kelebihan spin paralel terhadap medan magnet B0."),
        GlossaryItem("FID (Free Induction Decay)", "FID", "M01", "M01.T2.3", "Sinyal radiofrekuensi transien yang diinduksikan ke koil penerima segera setelah pulsa RF 90° dinonaktifkan."),
        GlossaryItem("SAR (Specific Absorption Rate)", "SAR", "M07", "M07.T2.2", "Laju daya radiofrekuensi yang diserap per satuan massa jaringan tubuh (W/kg). Batas normal whole body adalah 2 W/kg."),
        GlossaryItem("Ruang-k (k-Space)", "k", "M04B", "M04B.T2.1", "Matriks digital penyimpanan data mentah spasial-frekuensi sebelum dikonversi menjadi citra visual oleh algoritma FFT."),
        GlossaryItem("FFT (Fast Fourier Transform)", "FFT", "M04B", "M04B.T2.1", "Operasi matematika cepat yang mentransformasikan data gelombang frekuensi dan fase di ruang-k menjadi citra visual piksel 2D."),
        GlossaryItem("Aliasing (Wrap-around)", "Artefak", "M08", "M08.T1.2", "Artefak lipatan anatomi di luar FoV yang masuk ke sisi berlawanan citra karena pelanggaran Teorema Nyquist."),
        GlossaryItem("Chemical Shift", "Artefak", "M08", "M08.T1.4", "Pergeseran spasial relatif semu antara jaringan lemak dan air sebesar ~3.5 ppm akibat perbedaan shielding elektron molekuler."),
        GlossaryItem("ETL (Echo Train Length)", "ETL", "M09", "M09.T1.2", "Jumlah echo yang direkam dalam satu TR pada sekuens Turbo Spin Echo. Mempercepat akuisisi sebanding nilainya."),
        GlossaryItem("DWI (Diffusion Weighted Imaging)", "DWI", "M03C", "M03C.T1.1", "Pencitraan difusi molekuler air bebas berbasis gerak Brown mikro. Sensitif terhadap iskemia serebral hiperakut."),
        GlossaryItem("ADC (Apparent Diffusion Coefficient)", "ADC", "M03C", "M03C.T1.1", "Peta kuantitatif laju difusi air jaringan tanpa pengaruh kontras T2 shinethrough."),
        GlossaryItem("FLAIR (Fluid Attenuated IR)", "FLAIR", "M03A", "M03A.T1.3", "Sekuens inversi recovery dengan TI panjang untuk memadamkan sinyal air bebas (CSF), menonjolkan lesi periventrikular."),
        GlossaryItem("STIR (Short TI IR)", "STIR", "M03A", "M03A.T1.3", "Sekuens inversi recovery dengan TI pendek (~150 ms) untuk memadamkan lemak, ideal untuk mendeteksi edema sumsum tulang."),
        GlossaryItem("Magic Angle", "Artefak", "M08", "M08.T2.4", "Peningkatan sinyal semu abnormal pada struktur tendon/ligamen berkolagen yang membentuk sudut ~55° terhadap medan B0."),
        GlossaryItem("Slew Rate", "SR", "M06", "M06A.T2.1", "Laju kecepatan gradien mencapai amplitudo maksimum (satuan T/m/s). Menentukan kecepatan sekuens EPI."),
        GlossaryItem("Quench", "Keselamatan", "M07", "M07B.T2.7", "Pelepasan mendadak gas helium cair dari cryostat magnet superkonduktif yang menyebabkan hilangnya medan B0 seketika.")
    )

    val defaultNotes = listOf(
        StudyNote(
            id = "note_1",
            title = "Catatan Kosong Saya (Klik untuk Menulis)",
            content = "Ruang catatan kosong personal Anda. Gunakan untuk mencatat hasil diskusi kasus, poin penting kuliah radiologi, atau trik pengaturan parameter pemindai MRI...",
            category = "Catatan Bebas",
            dateString = "Hari Ini"
        ),
        StudyNote(
            id = "note_2",
            title = "Ringkasan Rumus & Parameter Kunci",
            content = "1. Frekuensi Larmor: f₀ = γ · B₀ (Hidrogen: 42.58 MHz/T -> 1.5T: 63.87 MHz, 3T: 127.74 MHz)\n2. Waktu Pindai (TA): TA = (TR · N_fase · NEX) / ETL\n3. Pembobotan T1w: TR pendek (<600ms), TE pendek (<25ms)\n4. Pembobotan T2w: TR panjang (>2000ms), TE panjang (>80ms)",
            category = "Fisika",
            dateString = "Kemarin"
        ),
        StudyNote(
            id = "note_3",
            title = "Protokol MRI Kepala Rutin",
            content = "Urutan sekuens standar:\n1. T1W Aksial (anatomi umum, evaluasi korteks)\n2. T2W Aksial (edema, patologi cairan hiperintens)\n3. FLAIR Aksial (supresi CSF, deteksi lesi demielinasi)\n4. DWI / ADC (evaluasi stroke iskemik akut)\n5. T2* / SWI (deteksi mikro-perdarahan atau kalsifikasi)",
            category = "Protokol",
            dateString = "2 Hari Lalu"
        )
    )

    val quizzes = listOf(
        QuizQuestion(
            id = "Q1",
            moduleId = "M01",
            nodeCode = "M01.T1.5",
            question = "Berapakah perkiraan frekuensi presesi Larmor untuk proton hidrogen (¹H) pada pemindai MRI berkekuatan 1.5 Tesla? (γ = 42.58 MHz/T)",
            options = listOf("~42.58 MHz", "~63.87 MHz", "~127.74 MHz", "~21.29 MHz"),
            correctIndex = 1,
            explanation = "Sesuai rumus Larmor f₀ = γ · B₀: 42.58 MHz/T × 1.5 T = 63.87 MHz."
        ),
        QuizQuestion(
            id = "Q2",
            moduleId = "M01",
            nodeCode = "M01.T1.1",
            question = "Mengapa proton Hidrogen (¹H) dijadikan inti pilihan utama dalam pencitraan MRI klinis rutin?",
            options = listOf(
                "Karena inti hidrogen tidak memiliki spin kuantum",
                "Karena bersifat radioaktif dan mudah meluruh",
                "Karena memiliki momentum magnetik kuat dan kelimpahan air/lemak tertinggi dalam tubuh",
                "Karena frekuensi presesinya selalu bernilai nol di dalam medan magnet"
            ),
            correctIndex = 2,
            explanation = "Tubuh manusia kaya akan air (H₂O) dan lipid. Inti hidrogen adalah proton tunggal ganjil dengan momen magnetik bersih yang melimpah."
        ),
        QuizQuestion(
            id = "Q3",
            moduleId = "M02",
            nodeCode = "M02.T1.4",
            question = "Kombinasi parameter TR dan TE manakah yang menghasilkan pembobotan citra T1-Weighted (T1w)?",
            options = listOf(
                "TR Panjang (>2000 ms) dan TE Panjang (>80 ms)",
                "TR Pendek (<600 ms) dan TE Pendek (<25 ms)",
                "TR Panjang (>2000 ms) dan TE Pendek (<25 ms)",
                "TR Sangat Panjang (>4000 ms) dan TE Sangat Panjang (>120 ms)"
            ),
            correctIndex = 1,
            explanation = "T1w membutuhkan TR pendek untuk memaksimalkan perbedaan pemulihan longitudinal dan TE pendek untuk meminimalkan pengaruh peluruhan transversal T2."
        ),
        QuizQuestion(
            id = "Q4",
            moduleId = "M03A",
            nodeCode = "M03A.T1.3",
            question = "Pada sekuens Inversion Recovery, teknik manakah yang dirancang khusus untuk memadamkan sinyal Cairan Serebrospinal (CSF)?",
            options = listOf(
                "STIR (Short TI Inversion Recovery)",
                "FLAIR (Fluid Attenuated Inversion Recovery)",
                "DWI (Diffusion Weighted Imaging)",
                "TOF (Time of Flight)"
            ),
            correctIndex = 1,
            explanation = "FLAIR menggunakan Inversion Time (TI) panjang (~2000-2500 ms) agar pulsa 90° dikirim saat magnetisasi CSF tepat di titik nol (null point)."
        ),
        QuizQuestion(
            id = "Q5",
            moduleId = "M07",
            nodeCode = "M07.T1.1",
            question = "Berdasarkan pedoman 4 Zona Keselamatan ACR, di zona manakah mesin pemindai magnetik MRI berada?",
            options = listOf("Zona I", "Zona II", "Zona III", "Zona IV"),
            correctIndex = 3,
            explanation = "Zona IV adalah ruang magnet scanner MRI sesungguhnya, area dengan medan statis dan bahaya proyektil tertinggi."
        ),
        QuizQuestion(
            id = "Q6",
            moduleId = "M09",
            nodeCode = "M09.T1.2",
            question = "Jika waktu TR = 500 ms, jumlah fase N_fase = 256, dan NEX = 2 pada sekuens 2D Spin Echo konvensional, berapa lama Waktu Akuisisi (TA)?",
            options = listOf("128 detik", "256 detik (~4.2 menit)", "512 detik", "64 detik"),
            correctIndex = 1,
            explanation = "TA = TR × N_fase × NEX = 0.5 detik × 256 × 2 = 256 detik (4 menit 16 detik)."
        )
    )
}
