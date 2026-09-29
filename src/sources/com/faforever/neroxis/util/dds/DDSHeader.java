package com.faforever.neroxis.util.dds;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;
import java.util.Objects;
import lombok.Generated;

/* JADX INFO: loaded from: app_classes.zip:com/faforever/neroxis/util/dds/DDSHeader.class */
public class DDSHeader {
    public static final int CAPS_FLAG = 1;
    public static final int HEIGHT_FLAG = 2;
    public static final int WIDTH_FLAG = 4;
    public static final int PITCH_FLAG = 8;
    public static final int PIXELFORMAT_FLAG = 4096;
    public static final int MIPMAPCOUNT_FLAG = 131072;
    public static final int LINEARSIZE_FLAG = 524288;
    public static final int DEPTH_FLAG = 8388608;
    public static final int ALPHAPIXELS_FLAG = 1;
    public static final int FOURCC_FLAG = 4;
    public static final int RGB_FLAG = 64;
    public static final int COMPLEX_FLAG = 8;
    public static final int TEXTURE_FLAG = 4096;
    public static final int MIPMAP_FLAG = 4194304;
    public static final int CUBEMAP_FLAG = 512;
    public static final int CUBEMAP_POSITIVEX_FLAG = 1024;
    public static final int CUBEMAP_NEGATIVEX_FLAG = 2048;
    public static final int CUBEMAP_POSITIVEY_FLAG = 4096;
    public static final int CUBEMAP_NEGATIVEY_FLAG = 8192;
    public static final int CUBEMAP_POSITIVEZ_FLAG = 16384;
    public static final int CUBEMAP_NEGATIVEZ_FLAG = 32768;
    public static final int VOLUME_FLAG = 2097152;
    private int flags;
    private int height;
    private int width;
    private int pitchOrLinearSize;
    private int depth;
    private int mipMapCount;
    private int pixelFlags;
    private String fourCC;
    private int RGBBitCount;
    private int RBitMask;
    private int GBitMask;
    private int BBitMask;
    private int ABitMask;
    private int caps1;
    private int caps2;
    private final String magic = "DDS ";
    private final int size = 124;
    private final int[] reserved1 = new int[11];
    private final int pixelFormatSize = 32;
    private int caps3 = 0;
    private int caps4 = 0;
    private int reserved2 = 0;
    private ByteBuffer headerBytesBuffer = ByteBuffer.allocate(128).order(ByteOrder.LITTLE_ENDIAN);

    @Generated
    public void setHeaderBytesBuffer(final ByteBuffer headerBytesBuffer) {
        this.headerBytesBuffer = headerBytesBuffer;
    }

    @Generated
    public void setPitchOrLinearSize(final int pitchOrLinearSize) {
        this.pitchOrLinearSize = pitchOrLinearSize;
    }

    @Generated
    public void setReserved2(final int reserved2) {
        this.reserved2 = reserved2;
    }

    @Generated
    public boolean equals(final Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof DDSHeader)) {
            return false;
        }
        DDSHeader other = (DDSHeader) o;
        if (!other.canEqual(this) || getSize() != other.getSize() || getPixelFormatSize() != other.getPixelFormatSize() || getFlags() != other.getFlags() || getHeight() != other.getHeight() || getWidth() != other.getWidth() || getPitchOrLinearSize() != other.getPitchOrLinearSize() || getDepth() != other.getDepth() || getMipMapCount() != other.getMipMapCount() || getPixelFlags() != other.getPixelFlags() || getRGBBitCount() != other.getRGBBitCount() || getRBitMask() != other.getRBitMask() || getGBitMask() != other.getGBitMask() || getBBitMask() != other.getBBitMask() || getABitMask() != other.getABitMask() || getCaps1() != other.getCaps1() || getCaps2() != other.getCaps2() || getCaps3() != other.getCaps3() || getCaps4() != other.getCaps4() || getReserved2() != other.getReserved2()) {
            return false;
        }
        Object this$magic = getMagic();
        Object other$magic = other.getMagic();
        if (this$magic == null) {
            if (other$magic != null) {
                return false;
            }
        } else if (!this$magic.equals(other$magic)) {
            return false;
        }
        if (!Arrays.equals(getReserved1(), other.getReserved1())) {
            return false;
        }
        Object this$headerBytesBuffer = getHeaderBytesBuffer();
        Object other$headerBytesBuffer = other.getHeaderBytesBuffer();
        if (this$headerBytesBuffer == null) {
            if (other$headerBytesBuffer != null) {
                return false;
            }
        } else if (!this$headerBytesBuffer.equals(other$headerBytesBuffer)) {
            return false;
        }
        Object this$fourCC = getFourCC();
        Object other$fourCC = other.getFourCC();
        if (this$fourCC == null) {
            return other$fourCC == null;
        }
        return this$fourCC.equals(other$fourCC);
    }

    @Generated
    protected boolean canEqual(final Object other) {
        return other instanceof DDSHeader;
    }

    @Generated
    public int hashCode() {
        int result = (1 * 59) + getSize();
        int result2 = (((((((((((((((((((((((((((((((((((result * 59) + getPixelFormatSize()) * 59) + getFlags()) * 59) + getHeight()) * 59) + getWidth()) * 59) + getPitchOrLinearSize()) * 59) + getDepth()) * 59) + getMipMapCount()) * 59) + getPixelFlags()) * 59) + getRGBBitCount()) * 59) + getRBitMask()) * 59) + getGBitMask()) * 59) + getBBitMask()) * 59) + getABitMask()) * 59) + getCaps1()) * 59) + getCaps2()) * 59) + getCaps3()) * 59) + getCaps4()) * 59) + getReserved2();
        Object $magic = getMagic();
        int result3 = (((result2 * 59) + ($magic == null ? 43 : $magic.hashCode())) * 59) + Arrays.hashCode(getReserved1());
        Object $headerBytesBuffer = getHeaderBytesBuffer();
        int result4 = (result3 * 59) + ($headerBytesBuffer == null ? 43 : $headerBytesBuffer.hashCode());
        Object $fourCC = getFourCC();
        return (result4 * 59) + ($fourCC == null ? 43 : $fourCC.hashCode());
    }

    @Generated
    public String toString() {
        return "DDSHeader(magic=" + getMagic() + ", size=" + getSize() + ", reserved1=" + Arrays.toString(getReserved1()) + ", pixelFormatSize=" + getPixelFormatSize() + ", headerBytesBuffer=" + String.valueOf(getHeaderBytesBuffer()) + ", flags=" + getFlags() + ", height=" + getHeight() + ", width=" + getWidth() + ", pitchOrLinearSize=" + getPitchOrLinearSize() + ", depth=" + getDepth() + ", mipMapCount=" + getMipMapCount() + ", pixelFlags=" + getPixelFlags() + ", fourCC=" + getFourCC() + ", RGBBitCount=" + getRGBBitCount() + ", RBitMask=" + getRBitMask() + ", GBitMask=" + getGBitMask() + ", BBitMask=" + getBBitMask() + ", ABitMask=" + getABitMask() + ", caps1=" + getCaps1() + ", caps2=" + getCaps2() + ", caps3=" + getCaps3() + ", caps4=" + getCaps4() + ", reserved2=" + getReserved2() + ")";
    }

    @Generated
    public String getMagic() {
        Objects.requireNonNull(this);
        return "DDS ";
    }

    @Generated
    public int getSize() {
        Objects.requireNonNull(this);
        return 124;
    }

    @Generated
    public int[] getReserved1() {
        return this.reserved1;
    }

    @Generated
    public int getPixelFormatSize() {
        Objects.requireNonNull(this);
        return 32;
    }

    @Generated
    public ByteBuffer getHeaderBytesBuffer() {
        return this.headerBytesBuffer;
    }

    @Generated
    public int getFlags() {
        return this.flags;
    }

    @Generated
    public int getHeight() {
        return this.height;
    }

    @Generated
    public int getWidth() {
        return this.width;
    }

    @Generated
    public int getPitchOrLinearSize() {
        return this.pitchOrLinearSize;
    }

    @Generated
    public int getDepth() {
        return this.depth;
    }

    @Generated
    public int getMipMapCount() {
        return this.mipMapCount;
    }

    @Generated
    public int getPixelFlags() {
        return this.pixelFlags;
    }

    @Generated
    public String getFourCC() {
        return this.fourCC;
    }

    @Generated
    public int getRGBBitCount() {
        return this.RGBBitCount;
    }

    @Generated
    public int getRBitMask() {
        return this.RBitMask;
    }

    @Generated
    public int getGBitMask() {
        return this.GBitMask;
    }

    @Generated
    public int getBBitMask() {
        return this.BBitMask;
    }

    @Generated
    public int getABitMask() {
        return this.ABitMask;
    }

    @Generated
    public int getCaps1() {
        return this.caps1;
    }

    @Generated
    public int getCaps2() {
        return this.caps2;
    }

    @Generated
    public int getCaps3() {
        return this.caps3;
    }

    @Generated
    public int getCaps4() {
        return this.caps4;
    }

    @Generated
    public int getReserved2() {
        return this.reserved2;
    }

    public DDSHeader() {
        this.headerBytesBuffer.put("DDS ".getBytes(), 0, 4);
        this.headerBytesBuffer.putInt(4, 124);
        for (int i = 0; i < this.reserved1.length; i++) {
            this.headerBytesBuffer.putInt(32 + (i * 4), this.reserved1[i]);
        }
        this.headerBytesBuffer.putInt(76, 32);
        this.headerBytesBuffer.putInt(116, this.caps3);
        this.headerBytesBuffer.putInt(120, this.caps4);
        this.headerBytesBuffer.putInt(124, this.reserved2);
        setCapsFlag(true);
        setPixelFormatFlag(true);
        setWidthFlag(true);
        setHeightFlag(true);
        setTextureFlag(true);
    }

    public static DDSHeader parseHeader(byte[] bytes) {
        DDSHeader header = new DDSHeader();
        ByteBuffer headerBytesBuffer = ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN);
        byte[] magicBytes = new byte[4];
        headerBytesBuffer.get(magicBytes);
        String str = new String(magicBytes);
        Objects.requireNonNull(header);
        if (!str.equals("DDS ")) {
            throw new IllegalArgumentException("Not a valid DDS Header");
        }
        int inSize = headerBytesBuffer.getInt();
        Objects.requireNonNull(header);
        if (inSize != 124) {
            throw new IllegalArgumentException("Not a valid DDS Header");
        }
        header.setFlags(headerBytesBuffer.getInt());
        header.setHeight(headerBytesBuffer.getInt());
        header.setWidth(headerBytesBuffer.getInt());
        header.setPitchOrLinearSize(headerBytesBuffer.getInt());
        header.setDepth(headerBytesBuffer.getInt());
        header.setMipMapCount(headerBytesBuffer.getInt());
        for (int i = 0; i < header.reserved1.length; i++) {
            header.reserved1[i] = headerBytesBuffer.getInt();
        }
        int inPixelFormatSize = headerBytesBuffer.getInt();
        Objects.requireNonNull(header);
        if (inPixelFormatSize != 32) {
            throw new IllegalArgumentException("Not a valid DDS Header");
        }
        header.setPixelFlags(headerBytesBuffer.getInt());
        byte[] fourCCBytes = new byte[4];
        headerBytesBuffer.get(fourCCBytes);
        header.setFourCC(new String(fourCCBytes));
        header.setRGBBitCount(headerBytesBuffer.getInt());
        header.setRBitMask(headerBytesBuffer.getInt());
        header.setGBitMask(headerBytesBuffer.getInt());
        header.setBBitMask(headerBytesBuffer.getInt());
        header.setABitMask(headerBytesBuffer.getInt());
        header.setCaps1(headerBytesBuffer.getInt());
        header.setCaps2(headerBytesBuffer.getInt());
        header.setCaps3(headerBytesBuffer.getInt());
        header.setCaps4(headerBytesBuffer.getInt());
        header.setReserved2(headerBytesBuffer.getInt());
        return header;
    }

    public boolean getCapsFlag() {
        return (this.flags & 1) > 0;
    }

    public void setCapsFlag(boolean val) {
        if (val) {
            this.flags |= 1;
        } else {
            this.flags &= -2;
        }
        setFlags(this.flags);
    }

    public void setFlags(int flags) {
        this.flags = flags;
        this.headerBytesBuffer.putInt(8, flags);
    }

    public boolean getHeightFlag() {
        return (this.flags & 2) > 0;
    }

    public void setHeightFlag(boolean val) {
        if (val) {
            this.flags |= 2;
        } else {
            this.flags &= -3;
        }
        setFlags(this.flags);
    }

    public boolean getWidthFlag() {
        return (this.flags & 4) > 0;
    }

    public void setWidthFlag(boolean val) {
        if (val) {
            this.flags |= 4;
        } else {
            this.flags &= -5;
        }
        setFlags(this.flags);
    }

    public boolean getPitchFlag() {
        return (this.flags & 8) > 0;
    }

    public void setPitchFlag(boolean val) {
        if (val) {
            this.flags |= 8;
        } else {
            this.flags &= -9;
        }
        setFlags(this.flags);
    }

    public boolean getPixelFormatFlag() {
        return (this.flags & 4096) > 0;
    }

    public void setPixelFormatFlag(boolean val) {
        if (val) {
            this.flags |= 4096;
        } else {
            this.flags &= -4097;
        }
        setFlags(this.flags);
    }

    public boolean getMipMapCountFlag() {
        return (this.flags & MIPMAPCOUNT_FLAG) > 0;
    }

    public void setMipMapCountFlag(boolean val) {
        if (val) {
            this.flags |= MIPMAPCOUNT_FLAG;
        } else {
            this.flags &= -131073;
        }
        setFlags(this.flags);
    }

    public boolean getLinearSizeFlag() {
        return (this.flags & LINEARSIZE_FLAG) > 0;
    }

    public void setLinearSizeFlag(boolean val) {
        if (val) {
            this.flags |= LINEARSIZE_FLAG;
        } else {
            this.flags &= -524289;
        }
        setFlags(this.flags);
    }

    public boolean getDepthFlag() {
        return (this.flags & DEPTH_FLAG) > 0;
    }

    public void setDepthFlag(boolean val) {
        if (val) {
            this.flags |= DEPTH_FLAG;
        } else {
            this.flags &= -8388609;
        }
        setFlags(this.flags);
    }

    public boolean getAlphaPixelsFlag() {
        return (this.pixelFlags & 1) > 0;
    }

    public void setAlphaPixelsFlag(boolean val) {
        if (val) {
            this.pixelFlags |= 1;
        } else {
            this.pixelFlags &= -2;
        }
        setPixelFlags(this.pixelFlags);
    }

    public boolean getFourCCFlag() {
        return (this.pixelFlags & 4) > 0;
    }

    public void setFourCCFlag(boolean val) {
        if (val) {
            this.pixelFlags |= 4;
        } else {
            this.pixelFlags &= -5;
        }
        setPixelFlags(this.pixelFlags);
    }

    public boolean getRGBFlag() {
        return (this.pixelFlags & 64) > 0;
    }

    public void setRGBFlag(boolean val) {
        if (val) {
            this.pixelFlags |= 64;
        } else {
            this.pixelFlags &= -65;
        }
        setPixelFlags(this.pixelFlags);
    }

    public boolean getComplexFlag() {
        return (this.caps1 & 8) > 0;
    }

    public void setComplexFlag(boolean val) {
        if (val) {
            this.caps1 |= 8;
        } else {
            this.caps1 &= -9;
        }
        setCaps1(this.caps1);
    }

    public boolean getTextureFlag() {
        return (this.caps1 & 4096) > 0;
    }

    public void setTextureFlag(boolean val) {
        if (val) {
            this.caps1 |= 4096;
        } else {
            this.caps1 &= -4097;
        }
        setCaps1(this.caps1);
    }

    public void setCaps1(int caps1) {
        this.caps1 = caps1;
        this.headerBytesBuffer.putInt(108, caps1);
    }

    public boolean getMipMapFlag() {
        return (this.caps1 & MIPMAP_FLAG) > 0;
    }

    public void setMipMapFlag(boolean val) {
        if (val) {
            this.caps1 |= MIPMAP_FLAG;
        } else {
            this.caps1 &= -4194305;
        }
        setCaps1(this.caps1);
    }

    public boolean getCubeMapFlag() {
        return (this.caps2 & CUBEMAP_FLAG) > 0;
    }

    public void setCubeMapFlag(boolean val) {
        if (val) {
            this.caps2 |= CUBEMAP_FLAG;
        } else {
            this.caps2 &= -513;
        }
        setCaps2(this.caps2);
    }

    public void setCaps2(int caps2) {
        this.caps2 = caps2;
        this.headerBytesBuffer.putInt(112, caps2);
    }

    public boolean getCubeMapPositiveXFlag() {
        return (this.pixelFlags & CUBEMAP_POSITIVEX_FLAG) > 0;
    }

    public void setCubeMapPositiveXFlag(boolean val) {
        if (val) {
            this.caps2 |= CUBEMAP_POSITIVEX_FLAG;
        } else {
            this.caps2 &= -1025;
        }
        setCaps2(this.caps2);
    }

    public boolean getCubeMapNegativeXFlag() {
        return (this.pixelFlags & CUBEMAP_NEGATIVEX_FLAG) > 0;
    }

    public void setCubeMapNegativeXFlag(boolean val) {
        if (val) {
            this.caps2 |= CUBEMAP_NEGATIVEX_FLAG;
        } else {
            this.caps2 &= -2049;
        }
        setCaps2(this.caps2);
    }

    public boolean getCubeMapPositiveYFlag() {
        return (this.pixelFlags & 4096) > 0;
    }

    public void setCubeMapPositiveYFlag(boolean val) {
        if (val) {
            this.caps2 |= 4096;
        } else {
            this.caps2 &= -4097;
        }
        setCaps2(this.caps2);
    }

    public boolean getCubeMapNegativeYFlag() {
        return (this.pixelFlags & CUBEMAP_NEGATIVEY_FLAG) > 0;
    }

    public void setCubeMapNegativeYFlag(boolean val) {
        if (val) {
            this.caps2 |= CUBEMAP_NEGATIVEY_FLAG;
        } else {
            this.caps2 &= -8193;
        }
        setCaps2(this.caps2);
    }

    public boolean getCubeMapPositiveZFlag() {
        return (this.pixelFlags & CUBEMAP_POSITIVEZ_FLAG) > 0;
    }

    public void setCubeMapPositiveZFlag(boolean val) {
        if (val) {
            this.caps2 |= CUBEMAP_POSITIVEZ_FLAG;
        } else {
            this.caps2 &= -16385;
        }
        setCaps2(this.caps2);
    }

    public boolean getCubeMapNegativeZFlag() {
        return (this.pixelFlags & CUBEMAP_NEGATIVEZ_FLAG) > 0;
    }

    public void setCubeMapNegativeZFlag(boolean val) {
        if (val) {
            this.caps2 |= CUBEMAP_NEGATIVEZ_FLAG;
        } else {
            this.caps2 &= -32769;
        }
        setCaps2(this.caps2);
    }

    public boolean getVolumeFlag() {
        return (this.pixelFlags & VOLUME_FLAG) > 0;
    }

    public void setVolumeFlag(boolean val) {
        if (val) {
            this.caps2 |= VOLUME_FLAG;
        } else {
            this.caps2 &= -2097153;
        }
        setCaps2(this.caps2);
    }

    public void setHeight(int height) {
        this.height = height;
        this.headerBytesBuffer.putInt(12, height);
    }

    public void setWidth(int width) {
        this.width = width;
        this.headerBytesBuffer.putInt(16, width);
    }

    public void setPitchOrLinearSize(int pitchOrLinearSize, boolean compressed) {
        this.pitchOrLinearSize = pitchOrLinearSize;
        this.headerBytesBuffer.putInt(20, pitchOrLinearSize);
        if (pitchOrLinearSize != 0 && compressed) {
            setLinearSizeFlag(true);
            setPitchFlag(false);
        } else if (pitchOrLinearSize != 0) {
            setPitchFlag(true);
            setLinearSizeFlag(false);
        } else {
            setPitchFlag(false);
            setLinearSizeFlag(false);
        }
    }

    public void setDepth(int depth) {
        this.depth = depth;
        this.headerBytesBuffer.putInt(24, depth);
        setDepthFlag(depth != 0);
    }

    public void setMipMapCount(int mipMapCount) {
        this.mipMapCount = mipMapCount;
        this.headerBytesBuffer.putInt(28, mipMapCount);
        if (mipMapCount != 0) {
            setMipMapCountFlag(true);
            setMipMapFlag(true);
        } else {
            setMipMapCountFlag(false);
            setMipMapFlag(false);
        }
    }

    public void setPixelFlags(int pixelFlags) {
        this.pixelFlags = pixelFlags;
        this.headerBytesBuffer.putInt(80, pixelFlags);
    }

    public void setFourCC(String fourCC) {
        this.fourCC = fourCC;
        ByteBuffer fourCCByteBuffer = ByteBuffer.allocate(4);
        fourCCByteBuffer.put(fourCC.getBytes());
        byte[] fourCCbytes = fourCCByteBuffer.array();
        for (int i = 0; i < 4; i++) {
            this.headerBytesBuffer.put(84 + i, fourCCbytes[i]);
        }
        setFourCCFlag(fourCC.length() == 4);
        setRGBFlag(fourCC.length() != 4);
    }

    public void setRGBBitCount(int RGBBitCount) {
        this.RGBBitCount = RGBBitCount;
        this.headerBytesBuffer.putInt(88, RGBBitCount);
        setRGBFlag(RGBBitCount != 0);
        setFourCCFlag(RGBBitCount == 0);
    }

    public void setRBitMask(int RBitMask) {
        this.RBitMask = RBitMask;
        this.headerBytesBuffer.putInt(92, RBitMask);
    }

    public void setGBitMask(int GBitMask) {
        this.GBitMask = GBitMask;
        this.headerBytesBuffer.putInt(96, GBitMask);
    }

    public void setBBitMask(int BBitMask) {
        this.BBitMask = BBitMask;
        this.headerBytesBuffer.putInt(100, BBitMask);
    }

    public void setABitMask(int ABitMask) {
        this.ABitMask = ABitMask;
        this.headerBytesBuffer.putInt(104, ABitMask);
        setAlphaPixelsFlag(ABitMask != 0);
    }

    public void setCaps3(int caps3) {
        this.caps3 = caps3;
        this.headerBytesBuffer.putInt(116, caps3);
    }

    public void setCaps4(int caps4) {
        this.caps4 = caps4;
        this.headerBytesBuffer.putInt(120, caps4);
    }

    public byte[] toBytes() {
        return this.headerBytesBuffer.array();
    }
}
