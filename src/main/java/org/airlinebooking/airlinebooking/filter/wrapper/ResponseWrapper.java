package org.airlinebooking.airlinebooking.filter.wrapper;

import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpServletResponseWrapper;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

/*
 * HttpServletResponseWrapper Java EE’de gelen bir sınıf. Response davranışını sarmalayarak değiştirmene izin verir.
 *Burada response’un çıktısını kopyalayıp saklamak için kullanılıyor.
 *outputStreamWrapper: Orijinal response stream’i tutar.
 * writerWrapper: Response için yazıcı (PrintWriter) tutar.
 * copier: Response gövdesini kopyalayan özel ServletOutputStreamWrapper.
 *
 */
public class ResponseWrapper extends HttpServletResponseWrapper {
    private ServletOutputStream outputStreamWrapper;
    private PrintWriter writerWrapper;
    private ServletOutputStreamWrapper copier;//copier: ServletOutputStreamWrapper türünde bir nesne. Yanıtın içeriğini kopyalamak için kullanılır.

    public ResponseWrapper(HttpServletResponse response) {
        super(response);
    }
    public Map<String,String> getAllHeaders(){
        Map<String,String> headers=new HashMap<>();
        getHeaderNames().forEach(headerName->headers.put(headerName,getHeader(headerName)));
        return headers;
    }

    /*
     * Eğer response için getWriter() çağrılmışsa, getOutputStream() kullanılamaz (Servlet standardı).
     * copier yaratılır ve orijinal ServletOutputStream’i sarmalar.
     * Amaç: Response yazılırken aynı anda kopyasını da almak.
     * */
    @Override
    public ServletOutputStream getOutputStream() throws IOException {
        if (writerWrapper!=null){
            throw new IllegalStateException("getWriter() has already been called for this response.");
        }

        if (outputStreamWrapper==null){
            outputStreamWrapper=getResponse().getOutputStream();
            copier=new ServletOutputStreamWrapper(outputStreamWrapper);
        }
        return copier;
    }

    /*
     * Eğer getOutputStream() çağrılmışsa getWriter() kullanılamaz.
     * Burada da copier yaratılır ve response yazılırken aynı anda kopyalanır.
     * PrintWriter stream’i copier üzerinden yazıyor.
     * */
    @Override
    public PrintWriter getWriter() throws IOException {
        if (outputStreamWrapper != null) {
            throw new IllegalStateException("getOutputStream() has already been called on this response.");
        }

        if (writerWrapper == null) {
            copier = new ServletOutputStreamWrapper(getResponse().getOutputStream());
            writerWrapper = new PrintWriter(new OutputStreamWriter(copier, getResponse().getCharacterEncoding()), true);
        }
        return writerWrapper;
    }

    /*
     * Response buffer’ını boşaltır (flush).
     * Hem writerWrapper hem de copier flush edilir, böylece orijinal response ve kopya senkronize olur.
     * */
    @Override
    public void flushBuffer() throws IOException {
        if (writerWrapper != null) {
            writerWrapper.flush();
        } else if (outputStreamWrapper != null) {
            copier.flush();
        }
        super.flushBuffer(); // Gerçek response buffer'ı da flush edilsin
    }

    /*
     * Response’un yazılmış bütün içeriğini byte dizisi olarak döner.
     * Logger veya analiz amaçlı kullanılabilir.
     * */
    public byte[] getCopyBody() {
        if (copier != null) {
            return copier.getCopy();
        }
        return new byte[0];
    }
}
