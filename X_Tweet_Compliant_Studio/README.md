# X Tweet — Compliant Studio (v3.0.0)
Profesyonel arayüz, planlayıcı ve raporlamaya sahip, **Twitter/X kurallarına uygun** bir stüdyo.
- Tweet gönderme (compose), **reply** ve **quote** işlemleri için **resmi Twitter API v2** iskeleti
- **Cron planlayıcı** (Quartz)
- **CSV/JSON raporlama**
- **Manual Assist**: URL listelerini tarayıcıda açıp işlemleri **manuel** yapmanız için rehberlik

> Not: Resmi API ile gönderim yapmak için **kendi geliştirici hesabınızdan** alınmış kullanıcı bağlamlı OAuth erişim token'ı gerekir.
> Bu proje, platform kurallarını ihlal eden otomasyonları (otomatik beğeni, takip vs.) içermez.

## Kurulum
1) Java 17 kurulu olsun.
2) Proje klasöründe:
   ```bash
   mvn -q -DskipTests package
   ```
3) UI:
   ```bash
   mvn -q exec:java
   ```

## Yapı
- `ui/` — Swing arayüz (sekmeli: Ayarlar, Compose, Planlayıcı, Manual Assist, Log & Rapor)
- `api/TwitterApiClient` — Twitter API v2 skeleton (POST /2/tweets)
- `manage/` — Controller ve ScheduleManager
- `report/` — ActionRecord + Reporter (CSV/JSON)
- `service/ManualAssist` — URL açıcı (manuel adımlar için)
- `configs/` — Profil bazlı ayarlar (`default.properties` vb.)

## Ayar
- UI'dan **Twitter Access Token** girin (kullanıcı bağlamlı).
- Compose sekmesinde metni girip `Hemen Gönder` veya **Planlayıcı** ile cron ifadesi tanımlayın.
- Manual Assist sekmesinde URL listesini açarak adımları kendiniz uygulayın.

## Güvenlik & Uyum
- Otomatik beğeni/takip/RT gibi platform kurallarını ihlal eden eylemler **yoktur**.
- Bu stüdyo, kurallara uygun içerik planlama, gönderim ve raporlama için tasarlanmıştır.
