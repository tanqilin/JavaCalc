package calc;

//����ͼ�λ���
import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
// ������ʽ�İ�
import java.util.regex.*;

import java.util.List;
import java.util.ArrayList;
import java.lang.Integer;
// ������
public class MyCalc implements ActionListener {
	private JTextField filed;
	private JFrame frame;
	private JButton[] buttons;		// ��ť����
	private String str[] = {"��ʷ","���","�˸�","����","7","8","9","/","4","5","6","*","1","2","3","-",".","0","=","+"};
	private String inputStr = ""; 	// �û�����
	private List<String> history; 	// ������ʷ
	private int count = 0;			// �������
	private String[] inputNum;		// ���������,�������
	private String[] inputSymbol;	// ����������
	
	// ������
	public static void main(String[] args) {
		MyCalc calc = new MyCalc();
		calc.drowCalc();
	}
	
	// ���Ƽ���������
	public void drowCalc(){
		buttons = new JButton[20]; // 10�����ְ�ť��
		history = new ArrayList<String>();
		
		frame = new JFrame("̷���ֵļ�����");	// new һ���������������
		filed = new JTextField(50);
		Container winContent = frame.getContentPane(); // ��ȡ�������
		
		// �������������
		JPanel panel1 = new JPanel();
		JPanel panel2 = new JPanel();
		panel2.setLayout(new GridLayout(1,1)); // ʹ�ı�������������������

		// ��ʼ������
		this.showNumButton(panel1);
		this.showResult(panel2);

		// ������������
		winContent.add(panel1, BorderLayout.CENTER);
		winContent.add(panel2, BorderLayout.NORTH);

		// ��ʾ�������ڴ�������ʾ
		frame.setSize(350,300);
		frame.setLocation(400,200);
		frame.setResizable(false); // �̶����ڴ�С
		frame.setVisible(true);
	}
	
	// ��������ʾ��ť
	public void showNumButton(JPanel panel1){
		panel1.setLayout(new GridLayout(5,4));
		panel1.setSize(300,50);
		
		// New��������ť
		for(int i=0; i<20; i++){
			buttons[i] = new JButton(str[i]);	
			buttons[i].setSize(100, 50);
			buttons[i].addActionListener(this);
		}

		// �Ѱ�ť�Ž������
		for(int i=0; i<20; i++){
			panel1.add(buttons[i]);
		}
		
	}
		
	// ���Ƽ������ؼ�
	public void showResult(JPanel panel2){
		filed.setFont(new Font("����", Font.PLAIN, 30) );
		filed.setEditable(false);
		panel2.add(filed);
		panel2.setPreferredSize(new Dimension(0, 50));
	}
	
	// �����Ӧ����
	public void actionPerformed(ActionEvent e){
		// ((JButton)e.getSource()).getText())��ȡ�����ť�ϵ�ֵ // String.valueOf(inputStr); ����ת�ַ���

		switch(((JButton)e.getSource()).getText()){
			case "���":inputStr = "";break;
			case "��ʷ":showHistory();break;
			case "����":showHelpWin();break;
			case "�˸�":
				if(inputStr.length() > 0)
					inputStr = inputStr.substring(0,inputStr.length()-1);
				break;
			case ".":;;break;
			default:
				inputStr += ((JButton)e.getSource()).getText();
				break;
		}
		filed.setText(inputStr);
		
		// �����˵��ڣ���ʼ����
		if(((JButton)e.getSource()).getText() == "="){
			this.replace11();
		}
	}
	
	// �����˵���
	private void replace11() {
        //���û�������ַ�������ȡ���������
        inputNum = inputStr.replaceAll("\\+|\\-|\\*|\\/|\\=", "_").split("_"); 	// ��ȡ������ֵ
        inputSymbol = inputStr.replaceAll("(\\d+)|\\=", "_").split("_"); 		// ��ȡ���������
    
        if(inputSymbol.length > inputNum.length){
        	filed.setText("��ʽ�������");
        	return;
        }
        
        try{
	        while(inputSymbol.length > 1){
	        	// �г˳�����˳�����
	        	if(getIndex("*") > (-1) && getIndex("/") > (-1)){
	        		if(getIndex("*") < getIndex("/")){
		        		multiplication();
		        	}else{
		        		division();
		        	}
	        	}else{
					if(getIndex("*") > (-1)) multiplication();
					if(getIndex("/") > (-1)) division();
				}
	        	
	        	// �˳��������������Ӽ�
				if(getIndex("*") < 0 && getIndex("/") < 0){
					if(getIndex("+") > (-1) && getIndex("-") > (-1)){
						if(getIndex("+") < getIndex("-"))
							addFun();
						else
							subtraction();
					}else{
						if(getIndex("+") > (-1)) addFun();		
						if(getIndex("-") > (-1)) subtraction();
					}
				}
	        }
        }catch(Exception e){
        	filed.setText("��ʽ�������");
        	inputStr = "";
        	return;
        }
        
        inputStr += inputNum[0];
        history.add(inputStr);
        filed.setText(inputStr); 	// ��ʾ������
        inputStr = inputNum[0]; 	// �ѽ���������� ������������
    }
	
	// �ӷ�
	public void addFun(){
		int value = getIndex("+");
		Integer leftValue = Integer.parseInt(inputNum[value-1]);
		Integer rightValue = Integer.parseInt(inputNum[value]);

		inputNum[value-1] = Integer.toString(leftValue + rightValue);
		
		// ɾ�������е�Ԫ��
		inputSymbol = removeElement(inputSymbol,value);
		inputNum = removeElement(inputNum,value);
	}
	
	// ����
	public void subtraction(){
		int value = getIndex("-");
		Integer leftValue = Integer.parseInt(inputNum[value-1]);
		Integer rightValue = Integer.parseInt(inputNum[value]);

		inputNum[value-1] = Integer.toString(leftValue - rightValue);
		
		// ɾ�������е�Ԫ��
		inputSymbol = removeElement(inputSymbol,value);
		inputNum = removeElement(inputNum,value);
	}
	
	// �˷�
	public void multiplication(){
		int value = getIndex("*");
		Integer leftValue = Integer.parseInt(inputNum[value-1]);
		Integer rightValue = Integer.parseInt(inputNum[value]);

		inputNum[value-1] = Integer.toString(leftValue * rightValue);
		
		// ɾ�������е�Ԫ��
		inputSymbol = removeElement(inputSymbol,value);
		inputNum = removeElement(inputNum,value);
	}
	
	// ����
	public void division(){
		int value = getIndex("/");
		Integer leftValue = Integer.parseInt(inputNum[value-1]);
		Integer rightValue = Integer.parseInt(inputNum[value]);

		inputNum[value-1] = Integer.toString(leftValue / rightValue);
		
		// ɾ�������е�Ԫ��
		inputSymbol = removeElement(inputSymbol,value);
		inputNum = removeElement(inputNum,value);
	}
	
	// ��ʾ��ʷ�����¼
	public void showHistory(){
		String[] strHistory = history.toArray(new String[1]);
		try{
			if(strHistory[count] != null){
				inputStr = strHistory[count];
				count++;
			}
		}
		catch(Exception e){
			count = 0;
		}
		
	}
	
	// ��ȡ��������±�ֵ
	public int getIndex(String x) {  
        for (int i = 0; i < inputSymbol.length; i++) {  
            if (inputSymbol[i].equals(x)) {  
                return i;  
            }  
        }  
        return -1;  //��������û���򷵻�-1  
    } 
	
	//ɾ��������ĳһ��Ԫ��  ,����������
    public static String[] removeElement(String[] array,int num) {  
        List<String> list = new ArrayList<String>();  
        for (int i=0; i<array.length; i++) {  
            list.add(array[i]);  
        }  
        
        list.remove(num); // ɾ��ָ���±�Ԫ��
        
        String[] newStr =  list.toArray(new String[1]); //����һ���������ж����ָ�����͵�����   

        return newStr;
    }
    
    public void showHelpWin(){
    	JFrame help = new JFrame("����");	// new һ���������������
    	Container helpWin = help.getContentPane(); // ��ȡ�������
    	JPanel helpPanel = new JPanel(new GridLayout(5,1));
    	helpPanel.add(new JLabel("��	       �ߣ�̷ �� ��"));
    	helpPanel.add(new JLabel("��	      �ڣ�2017-5-18"));
    	helpPanel.add(new JLabel("˵        ����ÿ�μ������������գ�"));
    	helpPanel.add(new JLabel("��        �ܣ�֧�ֶ�������������������������㣡"));
    	helpPanel.add(new JLabel("�汾��Ϣ��Ŀǰ��֧��С�����㣡"));
 
    	helpWin.add(helpPanel,BorderLayout.CENTER);
    	
    	// ��ʾ�������ڴ�������ʾ
    	help.setSize(300,200);
    	help.setLocation(420,250);	// ���ô�����ʾλ��
    	help.setResizable(false); // �̶����ڴ�С
    	help.setVisible(true);
    }
}












