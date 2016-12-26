package lgcCompiler;

public class Error {
    public  int errCount = 0;
    public static final String[] errInfo = new String[]{
        "",
        "1.Ӧ��=������:=",
        "2.=��ӦΪ��",
        "3.��ʶ����ӦΪ=",
        "4.const,var,procedure ��ӦΪ��ʶ��",
        "5.©�����Ż�ֺ�",
        "6.����˵����ķ��Ų���ȷ",
        "7.ӦΪ���",
        "8.������������ķ��Ų���ȷ",
        "9.ӦΪ���",
        "10.���֮��©�ֺ�",
        "11.��ʶ��δ˵��",
        "12.�����������������ֵ",
        "13.ӦΪ��ֵ�����:=",
        "14.call��ӦΪ��ʶ��",
        "15.���ɵ��ó��������",
        "16.ӦΪthen",
        "17.ӦΪ�ֺŻ�end",
        "18.ӦΪdo",
        "19.����ķ��Ų���ȷ",
        "20.ӦΪ��ϵ�����",
        "21.���ʽ�ڲ����й��̱�ʶ��",
        "22.©������",
        "23.���Ӻ󲻿�Ϊ�˷���",
        "24.���ʽ�����Դ˷��ſ�ʼ",
        "25.�����̫��",
        "26.Not Defined Yet",
        "27.Not Defined Yet",
        "28.Not Defined Yet",
        "29.Not Defined Yet",
        "30.Not Defined Yet",    
        "31.��Խ��",
        "32.Ƕ�ײ�������",
        "33.��ʽ����ӦΪ������",
        "34.��ʽ����ӦΪ������",
        "35.read()�еı���δ����",
        "36.����̫����",
        "37.δ֪����"
    };

    /**
     * ��ӡ������Ϣ
     * @param errcode 
     */
    public  void report(int errcode,int line) {
        try {
            System.out.println("*** line( "+line+"):" + errInfo[errcode] + "  ***");
            errCount++;
        }catch(Exception e){
            e.printStackTrace();
            System.out.println("***�ڴ�����ʱ������***");
        }

    }
}
