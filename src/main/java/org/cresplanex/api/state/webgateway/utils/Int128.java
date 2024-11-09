package org.cresplanex.api.state.webgateway.utils;

/**
 * 128ビットの整数を表すクラス。
 * <p>
 * 高位（hi）および低位（lo）の64ビットのlong値によって構成されます。 主にユニークな識別子として利用することができます。
 * </p>
 */
public class Int128 {

    private final long hi;
    private final long lo;

    /**
     * 指定された高位（hi）および低位（lo）の64ビット値で128ビット整数を初期化します。
     *
     * @param hi 64ビットの高位部分
     * @param lo 64ビットの低位部分
     */
    public Int128(long hi, long lo) {
        this.hi = hi;
        this.lo = lo;
    }

    /**
     * 128ビット整数を16進数の文字列形式で返します。 フォーマットは「{hiの16進数}-{loの16進数}」となります。
     *
     * @return 128ビット整数の16進数形式の文字列
     */
    public String asString() {
        return String.format("%016x-%016x", hi, lo);
    }

    /**
     * オブジェクトの文字列表現を返します。
     *
     * @return "Int128{hi-lo}" の形式の文字列
     */
    @Override
    public String toString() {
        return "Int128{" + asString() + '}';
    }

    /**
     * 他のオブジェクトとこのInt128オブジェクトが等しいかどうかを比較します。
     *
     * @param o 比較対象のオブジェクト
     * @return このオブジェクトが指定されたオブジェクトと等しい場合は true
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Int128 int128 = (Int128) o;

        return hi == int128.hi && lo == int128.lo;
    }

    /**
     * Int128オブジェクトのハッシュコードを計算して返します。
     *
     * @return このオブジェクトのハッシュコード
     */
    @Override
    public int hashCode() {
        int result = (int) (hi ^ (hi >>> 32));
        result = 31 * result + (int) (lo ^ (lo >>> 32));
        return result;
    }

    /**
     * 文字列からInt128オブジェクトを生成します。 フォーマットは「{hiの16進数}-{loの16進数}」である必要があります。
     *
     * @param str 変換対象の文字列
     * @return 生成されたInt128オブジェクト
     * @throws IllegalArgumentException フォーマットが無効な場合
     */
    public static Int128 fromString(String str) {
        String[] s = str.split("-");
        if (s.length != 2) {
            throw new IllegalArgumentException("Should have length of 2: " + str);
        }
        return new Int128(Long.parseUnsignedLong(s[0], 16), Long.parseUnsignedLong(s[1], 16));
    }

    /**
     * このInt128オブジェクトと他のInt128オブジェクトを比較します。
     *
     * @param other 比較対象のInt128オブジェクト
     * @return 比較結果。負の場合はこのオブジェクトがotherより小さい、0の場合は等しい、正の場合は大きい
     */
    public int compareTo(Int128 other) {
        int x = Long.compare(hi, other.hi);
        return x == 0 ? Long.compare(lo, other.lo) : x;
    }

    /**
     * 高位（hi）の64ビット値を取得します。
     *
     * @return 高位の64ビット値
     */
    public long getHi() {
        return hi;
    }

    /**
     * 低位（lo）の64ビット値を取得します。
     *
     * @return 低位の64ビット値
     */
    public long getLo() {
        return lo;
    }
}
