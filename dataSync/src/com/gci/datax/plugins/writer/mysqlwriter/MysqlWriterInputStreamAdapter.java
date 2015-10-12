package com.gci.datax.plugins.writer.mysqlwriter;

import com.gci.datax.common.plugin.Line;
import com.gci.datax.common.plugin.LineReceiver;
import com.gci.datax.common.util.StrUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;


public class MysqlWriterInputStreamAdapter extends InputStream {
    
    private LineReceiver receiver = null;

    private static final char[] replaceChars = {'\001', 0, '\002', 0};
    
	private int lineCounter = 0;
    /* 列分隔符 */
    private char sep = '\001';
    /* 行分隔符 */
    private final char BREAK = '\002';
    /* NULL字面字符*/
    private final String NULL = "\\N";

    private String encoding = "UTF8";
    
    private Line line = null;
    /* 从line中获取一行数据暂存数组*/
    private byte buffer[] = null;
    
    private StringBuilder lineBuilder = new StringBuilder(1024 * 1024 * 8);
    
    /* 存放上次余下 */
    private byte[] previous = new byte[1024 * 1024 * 8];
    /* 上次余留数据长度 */
    private int preLen = 0;
    /* 上次余留数据起始偏移 */
    private int preOff = 0;

    
    public MysqlWriterInputStreamAdapter(LineReceiver reader, MysqlWriter writer) {
        super();
        this.receiver = reader;
        this.sep = writer.getSep();
        this.encoding = writer.getEncoding();
    }

    
    @Override
    public int read(byte[] buff, int off, int len) throws IOException {
        if (buff == null) {
            throw new NullPointerException();
        } else if (off < 0 || len < 0 || len > buff.length - off) {
            throw new IndexOutOfBoundsException();
        } else if (len == 0) {
            return 0;
        }
        
        //System.out.printf(String.format("Len: %d\n", len));
        int total = 0;
        int read = 0;
        while (len > 0) {
            read = this.fetchLine(buff, off, len);
            if (read < 0) {
                break;
            }
            off += read;
            len -= read;
            total += read;
        }
        
        if (total == 0)
          return (-1);

        return total;
    }

    private void buildString(Line line) {
        lineBuilder.setLength(0);
        String field;
        for (int i = 0, num = line.getFieldNum();
                i < num; i++) {
            field = line.getField(i);
            if (null != field) {
                field = field.replace("\\", "\\\\");
                lineBuilder.append(StrUtils.replaceChars(field, replaceChars));
            } else {
                lineBuilder.append("\\N");
            }
            if (i < num - 1)
              lineBuilder.append(this.sep);
            else {
                lineBuilder.append(this.BREAK);
            }
        }
    }
    
    private int fetchLine(byte[] buff, int off, int len) throws UnsupportedEncodingException {
        int ret = 0;
        int currLen;
        
        /* 查看上次是否有剩余 */
        if (this.preLen > 0) {
            currLen = Math.min(this.preLen, len);
            System.arraycopy(this.previous, this.preOff, buff, off, currLen);
            this.preOff += currLen;
            this.preLen -= currLen;
            off += currLen;
            len -= currLen;
            ret += currLen;
            
            /* 如果buff比较小，上次余下的数据 */
            if (this.preLen > 0) {
                return ret;
            }
        }
        
        /* 本次读数据的逻辑 */
        int lineLen;
        int lineOff = 0;
        line = this.receiver.getFromReader();
        /* line为空，表明数据已全部读完 */
        if (line == null) {
            if (ret == 0)
                return (-1);
            return ret;
        }
        
		this.lineCounter++;
        this.buildString(line);
        this.buffer = lineBuilder.toString().getBytes(this.encoding);
        lineLen = this.buffer.length;
        currLen = Math.min(lineLen, len);
        System.arraycopy(this.buffer, 0, buff, off, currLen);
        len -= currLen;
        lineOff +=currLen;
        lineLen -= currLen;
        ret += currLen;
        /* len > 0 表明这次fetchLine还没有将buff填充完毕, buff有剩佄1�7 留作下次填充 */
        if (len > 0) {
            return ret;
        }
        
        /* 该buffer已经不够放一个line，因此把line的内容保存下来，供下丄1�7次fetch使用 
         * 这里的假设是previous足够处1�7 绝对够容纳一个line的内宄1�7 */
        /* fix bug: */
        if (lineLen > this.previous.length) {
            this.previous = new byte[lineLen << 1];
        }
        System.arraycopy(this.buffer, lineOff, this.previous, 0, lineLen);
        this.preOff = 0;
        this.preLen = lineLen;
        return (ret);
    }
    
    @Override
    public int read() throws IOException {
        /*
         * 注意: 没有实现read()
         * */
        throw new IOException("Read() is not supported");
    }
    
	public int getLineNumber() {
		return this.lineCounter;
	}

}
