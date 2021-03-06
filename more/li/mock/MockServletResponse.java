package li.mock;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.Locale;

import javax.servlet.ServletOutputStream;
import javax.servlet.ServletResponse;

import li.util.Log;
import li.util.ThreadUtil;

/**
 * MockServletResponse
 * 
 * @author li (limingwei@mail.com)
 * @version 0.1.1 (2012-09-27)
 */
class MockServletResponse implements ServletResponse {
    private static final Log log = Log.init();

    private Locale locale = Locale.getDefault();

    private String characterEncoding = "UTF-8";

    private String contentType;

    private Writer writer;

    public void setWritter(Writer writer) {
        this.writer = writer;
    }

    public PrintWriter getWriter() throws IOException {
        log.debug("li.mock.MockServletResponse.getWriter() calling by " + ThreadUtil.stackTrace());
        if (!(null != this.writer && this.writer instanceof PrintWriter)) {
            this.writer = new PrintWriter(null == this.writer ? new NullWriter() : this.writer);
        }
        return (PrintWriter) this.writer;
    }

    public ServletOutputStream getOutputStream() throws IOException {
        log.debug("li.mock.MockServletResponse.getOutputStream() calling by " + ThreadUtil.stackTrace());

        return new ServletOutputStream() {
            public void write(int b) throws IOException {
                getWriter().write(b);
            }
        };
    }

    public String getCharacterEncoding() {
        return this.characterEncoding;
    }

    public void setCharacterEncoding(String characterEncoding) {
        this.characterEncoding = characterEncoding;
    }

    public Locale getLocale() {
        return this.locale;
    }

    public void setLocale(Locale locale) {
        this.locale = locale;
    }

    public String getContentType() {
        return this.contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public void setBufferSize(int bufferSize) {}

    public void setContentLength(int contentLength) {}

    public int getBufferSize() {
        return 0;
    }

    public boolean isCommitted() {
        return false;
    }

    public void reset() {}

    public void resetBuffer() {}

    public void flushBuffer() throws IOException {}
}