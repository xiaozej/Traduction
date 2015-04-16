 import java.awt.FlowLayout;
 import java.awt.event.ActionEvent;
 import java.awt.event.ActionListener;
 import javax.swing.JButton;
 import javax.swing.JFileChooser;
 import javax.swing.JFrame;
 public class Blue1  {
   static JFrame f  = new JFrame();
   static JButton jb = new JButton("上传");
  public void Show() {
   jb.addActionListener(new ActionListener() {
    public void actionPerformed(ActionEvent e) {
     JFileChooser jfc = new JFileChooser();
     
     if(jfc.showOpenDialog(f)==JFileChooser.APPROVE_OPTION ){
      //解释下这里,弹出个对话框,可以选择要上传的文件,如果选择了,就把选择的文件的绝对路径打印出来,有了绝对路径,通过JTextField的settext就能设置进去了,那个我没写
      System.out.println(jfc.getSelectedFile().getAbsolutePath());
     }
    }
   });
   //这下面的不用在意 一些设置
   f.add(jb);
   f.setLayout(new FlowLayout());
   f.setSize(480, 320);
//   f.setDefaultCloseOperation(EXIT_ON_CLOSE);
   f.setLocationRelativeTo(null);
   f.setVisible(true);
  }
 public static void main(String[] args) {
  // TODO Auto-generated method stub
  new  Blue1().Show();
 }
}
