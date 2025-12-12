// Di dalam class Player.java

public void interact() {
    // 1. Cek ada apa di depan muka Player?
    int tileID = gp.cChecker.checkStation(this); 
    
    // Ambil koordinat tile di depan (untuk akses array stationItems di GamePanel)
    util.TilePos frontPos = new util.TilePos(
        (x + solidArea.x + solidArea.width/2)/gp.tileSize, 
        (y + solidArea.y + solidArea.height/2)/gp.tileSize
    ).move(util.Direction.valueOf(direction.toUpperCase())); // Asumsi direction masih String "up"

    int col = frontPos.col;
    int row = frontPos.row;

    // Cek batas map
    if (col < 0 || col >= gp.maxScreenCol || row < 0 || row >= gp.maxScreenRow) return;

    BaseItem.Item itemOnTable = gp.stationItems[col][row];


    // AMBIL BARANG (Tangan Kosong, Meja Ada Barang)
    if (inventory == null && itemOnTable != null) {
        inventory = itemOnTable; // Ambil ke tangan
        gp.stationItems[col][row] = null; // Hapus dari meja
        System.out.println("Mengambil " + inventory.getName());
    }
    
    // TARUH BARANG (Tangan Ada Barang, Meja Kosong, Bukan Tembok)
    else if (inventory != null && itemOnTable == null && tileID != 1) { // ID 1 = Tembok
        
        // Cek: Jika meja adalah TEMPAT SAMPAH (misal ID 9)
        if (tileID == 9) {
            inventory = null; // Buang barang
            System.out.println("Barang dibuang!");
        } 
        // Cek: SERVING COUNTER (misal ID 5)
        else if (tileID == 5) {
            if (inventory instanceof BaseItem.Dish) {
                // Submit order
                gp.orderGen.checkDelivery((BaseItem.Dish) inventory);
                inventory = null; // Barang diserahkan
            }
        }
        // Meja Biasa / Kompor / Talenan
        else {
            gp.stationItems[col][row] = inventory; // Taruh di meja
            inventory = null; // Kosongkan tangan
        }
    }

    // test 3: PROSES MASAK/POTONG (Tangan Kosong, Meja Ada Bahan)
    else if (inventory == null && itemOnTable != null) {
        
        // INTERAKSI CUTTING STATION (ID 2)
        if (tileID == 2 && itemOnTable instanceof BaseItem.Ingredient) {
            BaseItem.Ingredient ing = (BaseItem.Ingredient) itemOnTable;
            ing.chop(); // Panggil method chop()
        }
        
        // INTERAKSI STOVE (ID 3) - Cek logic cooking
        // Biasanya butuh Pan/Pot, tapi logic simplenya:
        if (tileID == 3 && itemOnTable instanceof BaseItem.Ingredient) {
             BaseItem.Ingredient ing = (BaseItem.Ingredient) itemOnTable;
             // Trigger masak manual atau otomatis (tergantung desain game Anda)
             ing.cook(1.0); // Masak sebentar
        }
    }
    
    // test 4: COMBINE (Tangan Ada Barang, Meja Ada Barang)
    // Contoh: Menaruh Patty ke atas Piring
    else if (inventory != null && itemOnTable != null) {
        // Logic penggabungan (misal Piring + Roti = Burger)
        // Anda butuh logic khusus di sini
    }
}
