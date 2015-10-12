package com.gci.util;

/** 数值范围类 */
public class Range<T extends Number>
{
        private T begin;
        private T end;
        
        public Range()
        {
                
        }
        
        public Range(T begin, T end)
        {
                this.begin      = begin;
                this.end                = end;
        }

        public T getBegin()
        {
                return begin;
        }

        public void setBegin(T begin)
        {
                this.begin = begin;
        }

        public T getEnd()
        {
                return end;
        }

        public void setEnd(T end)
        {
                this.end = end;
        }
        
        @Override
        public String toString()
        {
                return String.format("{%s - %s}", begin, end);
        }
}
