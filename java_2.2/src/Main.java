import java.util.Scanner;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Random;

public class Main {

    static Scanner tara = new Scanner(System.in);
    static String girisYapilanOgrenciNo = null;

    public static void main(String[] args)
    {
        anaMenu();
    }

    public static void anaMenu() {
        System.out.println("Yapmak İstediğin İşlemi Seç: ");
        System.out.println("1- Giriş Yap \n2- Üye Ol \n0- Çıkış Yap");

        int secilen = tara.nextInt();
        tara.nextLine();

        switch (secilen) {
            case 1:
                girisYap();
                break;
            case 2:
                uyeOl();
                break;
            case 0:
                System.out.println("Sistemden Çıkış Yapılıyor...");
                return;  // Programdan çık
            default:
                System.out.println("Geçersiz Seçim!");
                break;
        }
    }

    public static void girisYap() {
        System.out.println("**** Giriş Ekranı ****");
        System.out.print("Öğrenci No: ");
        String ogrenciNo = tara.nextLine();
        System.out.print("Şifre: ");
        String sifre = tara.nextLine();

        try (Connection baglanti = MySqlBaglanti.getConnection()) {
            String sql = "SELECT * FROM uyeler WHERE ogrenci_no = ? AND sifre = ?";
            PreparedStatement ps = baglanti.prepareStatement(sql);
            ps.setString(1, ogrenciNo);
            ps.setString(2, sifre);

            var sonuc = ps.executeQuery();
            if (sonuc.next()) {
                System.out.println("Giriş başarılı! Hoş geldiniz, " + sonuc.getString("isim"));
                girisYapilanOgrenciNo = ogrenciNo;
                menu();
            } else {
                System.out.println("Giriş başarısız! Öğrenci no veya şifre hatalı.");
            }
        } catch (SQLException e) {
            System.out.println("Veritabanı hatası: " + e.getMessage());
        }
    }

    public static void uyeOl() {
        System.out.println("**** Üye Olma İşlemi ****");

        System.out.print("İsim: ");
        String isim = tara.nextLine();
        System.out.print("Öğrenci No: ");
        String ogrenciNo = tara.nextLine();
        System.out.print("Şifre: ");
        String sifre = tara.nextLine();

        try (Connection baglanti = MySqlBaglanti.getConnection()) {
            String sql = "INSERT INTO uyeler (isim, ogrenci_no, sifre, kalori) VALUES (?, ?, ?, 0)";
            PreparedStatement ps = baglanti.prepareStatement(sql);
            ps.setString(1, isim);
            ps.setString(2, ogrenciNo);
            ps.setString(3, sifre);

            int sonuc = ps.executeUpdate();
            if (sonuc > 0) {
                System.out.println("Üyelik başarılı! Giriş yapabilirsiniz.");
            } else {
                System.out.println("Üyelik başarısız oldu!");
            }
        } catch (SQLException e) {
            System.out.println("Veritabanı hatası: " + e.getMessage());
        }
    }

    public static void menu() {
        while (true) {
            System.out.println("**** MENÜ EKRANI ****");
            System.out.println("1- Kalori Hesapla \n2- Kalori Güncelle \n3- Spor Önerisi Yap \n4- Haftalık Spor Programı Öner \n0- Çıkış");
            int menuSecim = tara.nextInt();
            tara.nextLine();

            if (menuSecim == 1) {
                System.out.print("Egzersiz süresi (dakika): ");
                int sure = tara.nextInt();
                tara.nextLine();
                int kalori = kaloriHesapla(sure);
                System.out.println("Yakılan kalori: " + kalori);
            }
            else if (menuSecim == 2) {
                if (girisYapilanOgrenciNo == null) {
                    System.out.println("Lütfen önce giriş yapınız.");
                    continue;
                }
                System.out.print("Güncellemek istediğiniz kalori miktarını girin: ");
                int yeniKalori = tara.nextInt();
                tara.nextLine();
                kaloriGuncelle(girisYapilanOgrenciNo, yeniKalori);
            }
            else if (menuSecim == 3) {
                System.out.print("Kalori Girin: ");
                int kalori = tara.nextInt();
                tara.nextLine();
                sporOnerisiYap(kalori);
            }
            else if (menuSecim == 4) {
                haftalikPlaniGoster();
            }
            else if (menuSecim == 0) {
                System.out.println("Çıkış Yapılıyor...");
                System.exit(0);
            }
            else {
                System.out.println("Geçersiz Giriş");
            }
        }
    }

    public static int kaloriHesapla(int dakika) {
        return dakika * 5;
    }

    public static void kaloriGuncelle(String ogrenciNo, int kalori) {
        try (Connection baglanti = MySqlBaglanti.getConnection()) {
            String sql = "UPDATE uyeler SET kalori = ? WHERE ogrenci_no = ?";
            PreparedStatement ps = baglanti.prepareStatement(sql);
            ps.setInt(1, kalori);
            ps.setString(2, ogrenciNo);
            int updatedRows = ps.executeUpdate();
            if (updatedRows > 0) {
                System.out.println("Kalori başarıyla güncellendi.");
            } else {
                System.out.println("Kalori güncelleme başarısız: Kullanıcı bulunamadı.");
            }
        } catch (SQLException e) {
            System.out.println("Kalori güncelleme hatası: " + e.getMessage());
        }
    }

    public static void sporOnerisiYap(int kalori) {
        if (kalori < 0) {
            System.out.println("Negatif kalori değeri giremezsiniz!");
            return;
        }
        if (kalori <= 100) {
            System.out.println("Yürüyüş");
        } else if (kalori <= 200) {
            System.out.println("Koşu");
        } else if (kalori <= 300) {
            System.out.println("Bisiklet");
        } else if (kalori <= 400) {
            System.out.println("Yüzme");
        } else if (kalori <= 500) {
            System.out.println("Fonksiyonel Antrenman");
        } else if (kalori <= 600) {
            System.out.println("Kuvvet Antrenmanı");
        } else {
            System.out.println("HIIT");
        }
    }

    public static void haftalikPlaniGoster() {
        String[] sporlar = {"Yürüyüş", "Koşu", "Bisiklet", "Yüzme", "Fonksiyonel Antrenman", "Kuvvet Antrenmanı", "HIIT"};
        String[] gunler = {"Pazartesi", "Salı", "Çarşamba", "Perşembe", "Cuma", "Cumartesi", "Pazar"};

        System.out.println("Haftalık Spor Programı:");
        Random rnd = new Random();
        for (int i = 0; i < 7; i++) {
            int rastgele = rnd.nextInt(sporlar.length);
            System.out.println(gunler[i] + " = " + sporlar[rastgele]);
        }
    }

    public static String rastgeleSporSec() {
        String[] sporlar = {"Yürüyüş", "Koşu", "Bisiklet", "Yüzme", "Fonksiyonel Antrenman", "Kuvvet Antrenmanı", "HIIT"};
        Random rnd = new Random();
        int rastgele = rnd.nextInt(sporlar.length);
        return sporlar[rastgele];
    }
}
