package HW4;

public class StringHash {
    public static void main(String[] args) {
        String s1 = "a";
        String s2 = "aa";
        String s3 = "aaa";

        System.out.println(hashCodeForString(s1));
        System.out.println(hashCodeForString(s2));
        System.out.println(hashCodeForString(s3));
    }
    public static int hashCodeForString(String s) {
        if(s.length() == 1) {
            return s.charAt(0);
        }

        return hashCodeForString(s.substring(0, s.length() - 1))  * 31 + s.charAt(s.length() - 1);
    }
}
