package calc;

//导入图形化包
import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
// 正则表达式的包
import java.util.regex.*;

import java.util.List;
import java.util.ArrayList;
import java.lang.Integer;
// 计算器
public class MyCalc implements ActionListener {
	private JTextField filed;
	private JFrame frame;
	private JButton[] buttons;		// 按钮数组
	private String str[] = {"历史","清空","退格","帮助","7","8","9","/","4","5","6","*","1","2","3","-",".","0","=","+"};
	private String inputStr = ""; 	// 用户输入
	private List<String> history; 	// 计算历史
	private int count = 0;			// 计算次数
	private String[] inputNum;		// 输入的数字,计算参数
	private String[] inputSymbol;	// 输入的运算符
	
	// 主函数
	public static void main(String[] args) {
		MyCalc calc = new MyCalc();
		calc.drowCalc();
	}
	
	// 绘制计算器界面
	public void drowCalc(){
		buttons = new JButton[20]; // 10个数字按钮池
		history = new ArrayList<String>();
		
		frame = new JFrame("谭其林的计算器");	// new 一个容器，传入标题
		filed = new JTextField(50);
		Container winContent = frame.getContentPane(); // 获取容器句柄
		
		// 创建两个个面板
		JPanel panel1 = new JPanel();
		JPanel panel2 = new JPanel();
		panel2.setLayout(new GridLayout(1,1)); // 使文本框铺满整个顶部布局

		// 初始化界面
		this.showNumButton(panel1);
		this.showResult(panel2);

		// 把面板加入容器
		winContent.add(panel1, BorderLayout.CENTER);
		winContent.add(panel2, BorderLayout.NORTH);

		// 显示把容器在窗口中显示
		frame.setSize(350,300);
		frame.setLocation(400,200);
		frame.setResizable(false); // 固定窗口大小
		frame.setVisible(true);
	}
	
	// 创建并显示按钮
	public void showNumButton(JPanel panel1){
		panel1.setLayout(new GridLayout(5,4));
		panel1.setSize(300,50);
		
		// New计算器按钮
		for(int i=0; i<20; i++){
			buttons[i] = new JButton(str[i]);	
			buttons[i].setSize(100, 50);
			buttons[i].addActionListener(this);
		}

		// 把按钮放进面板中
		for(int i=0; i<20; i++){
			panel1.add(buttons[i]);
		}
		
	}
		
	// 绘制计算结果控件
	public void showResult(JPanel panel2){
		filed.setFont(new Font("宋体", Font.PLAIN, 30) );
		filed.setEditable(false);
		panel2.add(filed);
		panel2.setPreferredSize(new Dimension(0, 50));
	}
	
	// 点击相应函数
	public void actionPerformed(ActionEvent e){
		// ((JButton)e.getSource()).getText())获取点击按钮上的值 // String.valueOf(inputStr); 数组转字符串

		switch(((JButton)e.getSource()).getText()){
			case "清空":inputStr = "";break;
			case "历史":showHistory();break;
			case "帮助":showHelpWin();break;
			case "退格":
				if(inputStr.length() > 0)
					inputStr = inputStr.substring(0,inputStr.length()-1);
				break;
			case ".":;;break;
			default:
				inputStr += ((JButton)e.getSource()).getText();
				break;
		}
		filed.setText(inputStr);
		
		// 按下了等于，开始计算
		if(((JButton)e.getSource()).getText() == "="){
			this.replace11();
		}
	}
	
	// 按下了等于
	private void replace11() {
        //从用户输入的字符串中提取出输入参数
        inputNum = inputStr.replaceAll("\\+|\\-|\\*|\\/|\\=", "_").split("_"); 	// 获取输入数值
        inputSymbol = inputStr.replaceAll("(\\d+)|\\=", "_").split("_"); 		// 获取输入运算符
    
        if(inputSymbol.length > inputNum.length){
        	filed.setText("算式输入错误");
        	return;
        }
        
        try{
	        while(inputSymbol.length > 1){
	        	// 有乘除先算乘除运算
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
	        	
	        	// 乘除运算结束后再算加减
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
        	filed.setText("算式输入错误");
        	inputStr = "";
        	return;
        }
        
        inputStr += inputNum[0];
        history.add(inputStr);
        filed.setText(inputStr); 	// 显示运算结果
        inputStr = inputNum[0]; 	// 把结果赋给输入 便于连续运算
    }
	
	// 加法
	public void addFun(){
		int value = getIndex("+");
		Integer leftValue = Integer.parseInt(inputNum[value-1]);
		Integer rightValue = Integer.parseInt(inputNum[value]);

		inputNum[value-1] = Integer.toString(leftValue + rightValue);
		
		// 删除数组中的元素
		inputSymbol = removeElement(inputSymbol,value);
		inputNum = removeElement(inputNum,value);
	}
	
	// 减法
	public void subtraction(){
		int value = getIndex("-");
		Integer leftValue = Integer.parseInt(inputNum[value-1]);
		Integer rightValue = Integer.parseInt(inputNum[value]);

		inputNum[value-1] = Integer.toString(leftValue - rightValue);
		
		// 删除数组中的元素
		inputSymbol = removeElement(inputSymbol,value);
		inputNum = removeElement(inputNum,value);
	}
	
	// 乘法
	public void multiplication(){
		int value = getIndex("*");
		Integer leftValue = Integer.parseInt(inputNum[value-1]);
		Integer rightValue = Integer.parseInt(inputNum[value]);

		inputNum[value-1] = Integer.toString(leftValue * rightValue);
		
		// 删除数组中的元素
		inputSymbol = removeElement(inputSymbol,value);
		inputNum = removeElement(inputNum,value);
	}
	
	// 除法
	public void division(){
		int value = getIndex("/");
		Integer leftValue = Integer.parseInt(inputNum[value-1]);
		Integer rightValue = Integer.parseInt(inputNum[value]);

		inputNum[value-1] = Integer.toString(leftValue / rightValue);
		
		// 删除数组中的元素
		inputSymbol = removeElement(inputSymbol,value);
		inputNum = removeElement(inputNum,value);
	}
	
	// 显示历史计算记录
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
	
	// 获取运算符的下标值
	public int getIndex(String x) {  
        for (int i = 0; i < inputSymbol.length; i++) {  
            if (inputSymbol[i].equals(x)) {  
                return i;  
            }  
        }  
        return -1;  //若数组中没有则返回-1  
    } 
	
	//删除数组中某一个元素  ,返回新数组
    public static String[] removeElement(String[] array,int num) {  
        List<String> list = new ArrayList<String>();  
        for (int i=0; i<array.length; i++) {  
            list.add(array[i]);  
        }  
        
        list.remove(num); // 删除指定下标元素
        
        String[] newStr =  list.toArray(new String[1]); //返回一个包含所有对象的指定类型的数组   

        return newStr;
    }
    
    public void showHelpWin(){
    	JFrame help = new JFrame("帮助");	// new 一个容器，传入标题
    	Container helpWin = help.getContentPane(); // 获取容器句柄
    	JPanel helpPanel = new JPanel(new GridLayout(5,1));
    	helpPanel.add(new JLabel("作	       者：谭 其 林"));
    	helpPanel.add(new JLabel("日	      期：2017-5-18"));
    	helpPanel.add(new JLabel("说        明：每次计算结束后请清空！"));
    	helpPanel.add(new JLabel("功        能：支持多参数，多运算符的连续混合运算！"));
    	helpPanel.add(new JLabel("版本信息：目前不支持小数运算！"));
 
    	helpWin.add(helpPanel,BorderLayout.CENTER);
    	
    	// 显示把容器在窗口中显示
    	help.setSize(300,200);
    	help.setLocation(420,250);	// 设置串口显示位置
    	help.setResizable(false); // 固定窗口大小
    	help.setVisible(true);
    }
}












