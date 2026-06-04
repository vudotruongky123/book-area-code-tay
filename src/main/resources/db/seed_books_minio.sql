USE book_area1;
GO

UPDATE books
SET pdf_object_name = N'book/_cay-chuoi-non-di-giay-xanh.pdf',
    cover_object_name = N'anhbia/cay-chuoi-non-di-giay-xanh-pdf-epub-azw3-mobi.jpg'
WHERE id = 3;

UPDATE books
SET pdf_object_name = N'book/_di-tim-le-song.pdf',
    cover_object_name = N'anhbia/di-tim-le-song.png'
WHERE id = 8;

UPDATE books
SET pdf_object_name = N'book/_harry-potter-va-hon-da-phu-thuy.pdf',
    cover_object_name = N'anhbia/harrypotter.png'
WHERE id = 1;

UPDATE books
SET pdf_object_name = N'book/_mat-biec.pdf',
    cover_object_name = N'anhbia/matbiec.png'
WHERE id = 2;

UPDATE books
SET pdf_object_name = N'book/_nha-gia-kim.pdf',
    cover_object_name = N'anhbia/nhagiakim.png'
WHERE id = 4;

UPDATE books
SET pdf_object_name = N'book/_rung-na-uy.pdf',
    cover_object_name = N'anhbia/rungnauy.png'
WHERE id = 10;

UPDATE books
SET pdf_object_name = N'book/_sapiens-luoc-su-loai-nguoi.pdf',
    cover_object_name = N'anhbia/sapiens-luoc-su-loai-nguoi.png'
WHERE id = 7;

UPDATE books
SET pdf_object_name = N'book/_tuoi-tre-dang-gia-bao-nhieu.pdf',
    cover_object_name = N'anhbia/tui-tre-dang-gia-bao-nhieu.png'
WHERE id = 9;
GO
