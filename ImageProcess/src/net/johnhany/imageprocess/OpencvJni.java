package net.johnhany.imageprocess;  
  
public class OpencvJni {  
    public static native int[] gray(int[] pixels, int w, int h);
    public static native int[] edgeDetect(int[] pixels, int w, int h);
    public static native int[] contrastEnhance(int[] pixels, int w, int h);
    public static native int[] gaussianBlur(int[] pixels, int w, int h);
    public static native int[] motionBlur(int[] pixels, int w, int h);
    public static native int[] meanFilter(int[] pixels, int w, int h);
    public static native int[] touchGray(int[] pixels, int w, int h, float cx, float cy);
}  