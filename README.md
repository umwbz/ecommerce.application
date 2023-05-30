# ecommerce.application


Membuat backend API untuk aplikasi e-commerce sederhana dengan java
Spesifikasi API:
• API bertujuan untuk mengakses dan memanipulasi data setiap entitas dari
database
• Request method pada API:
• GET: untuk mendapatkan list atau detail data dari entitas
• POST: untuk membuat data entitas baru
• PUT: untuk mengubah data dari entitas
• DELETE: untuk menghapus data dari entitas
• Body pada request POST dan PUT memiliki format JSON
• Response yg diberikan oleh server API memiliki format JSON
• Otorisasi akses API menggunakan API key yg di set melalui ENV variable
• Data disimpan pada database SQLite
• Pengujian API dilakukan menggunakan aplikasi Postman
• Alamat Port menggunakan 018
Spesifikasi API GET:
• GET /users => daftar semua user
• GET /users/{id} => informasi user dan alamatnya, jika tidak ada beri HTTP eror
response beserta pesannya
• GET /users/{id}/products => daftar produk milik user
• GET /users/{id}/orders => daftar order milik user
• GET /users/{id}/reviews => daftar review yg dibuat user
• GET /orders/{id} => informasi order, buyer, order detail, review, produk: title,
price
• GET /products => daftar semua produk
• GET /products/{id} => informasi produk dan seller
• Filter dengan query params misal: GET
/products?field=stock&cond=largerEqual&val=10
Spesifikasi API POST dan PUT:
• Ketika membuat entitas baru, semua data harus lengkap. Jika tidak, maka
berikan HTTP error response (misal: 400 untuk BAD REQUEST), beserta
pesan errornya.
• Ketika melakukan update pada entitas, pastikan entitas memang ada. Jika
tidak, maka berikan HTTP error response beserta pesan errornya.
