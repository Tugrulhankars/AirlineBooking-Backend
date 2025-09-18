package org.airlinebooking.airlinebooking.filter.wrapper;

import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.WriteListener;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class ServletOutputStreamWrapper extends ServletOutputStream {

    private final ServletOutputStream outputStream;
    private final ByteArrayOutputStream copy=new ByteArrayOutputStream();

    public ServletOutputStreamWrapper(ServletOutputStream outputStream) {
        this.outputStream = outputStream;
    }

    //Response’a yazılan her byte, aynı anda kopyalanır.
    //Böylece response çıktısı hem client’a gönderilir hem de buffer’da saklanır.
    @Override
    public void write(int b) throws IOException {
        outputStream.write(b);
        copy.write(b);
    }

    @Override
    public void write(byte[] b, int off, int len) throws IOException {
        outputStream.write(b, off, len);
        copy.write(b, off, len);
    }

    //Flush ve close hem orijinal hem kopya stream’e uygulanır.
    @Override
    public void flush() throws IOException {
        outputStream.flush();
        copy.flush();
    }

    @Override
    public void close() throws IOException {
        outputStream.close();
        copy.close();
    }

    @Override
    public boolean isReady() {
        return outputStream.isReady();
    }

    //Async yazma (setWriteListener) orijinal stream’e devredilir.
    @Override
    public void setWriteListener(WriteListener writeListener) {
        outputStream.setWriteListener(writeListener);
    }

    /*
     * Response gövdesinin tüm kopyasını byte[] olarak döner.
     * Logger veya test amaçlı kullanılabilir.
     * */
    public byte[] getCopy() {
        return copy.toByteArray();
    }
}
