package com.sallai.cut.gui;

import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.FlatLightLaf;
import com.sallai.cut.adapter.BaseCustomMouseListener;
import com.sallai.cut.componet.HeaderJPanel;
import com.sallai.cut.utils.CutList;
import com.sallai.cut.utils.CutUtil;
import com.sallai.cut.utils.Notification;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseEvent;
import java.util.Collection;
import java.util.stream.IntStream;

import static com.sallai.cut.utils.AppConstant.HELLO_ITEM_INFO;

/**
 * @description: main gui$
 * @author: sallai
 * @time: 2022年2月27日 0027 上午 10:23:03 秒
 */
@Slf4j
public class MainGui {
    public static DefaultListModel<String> listModel = new DefaultListModel<>();
    public static DefaultListModel<String> searchListModel = new DefaultListModel<>();
    private static final JList<String> mJlist = new JList<>(listModel);
    public static long start = 0;
    public static int select_index = -1;
    private static JScrollPane jScrollPane;
    public static JFrame cutHelper;
    private JPopupMenu popupMenu;
    private static boolean searchFlag = false;

    public void initGui() {

        FlatLightLaf.setup();
        FlatDarkLaf.setup();
        listModel.addElement(HELLO_ITEM_INFO);
        //舒适化窗口
        cutHelper = new JFrame("CutHelper");
        // 设置窗口大小
        cutHelper.setSize(300, 400);
        cutHelper.setResizable(true);
        //设置图标
        Image icon = Toolkit.getDefaultToolkit().getImage(Notification.class.getResource("/img/cut.png"));
        cutHelper.setIconImage(icon);
        // 把窗口位置设置到屏幕中心
        cutHelper.setLocationRelativeTo(null);
        // 当点击窗口的关闭按钮时退出程序（没有这一句，程序不会退出）
        cutHelper.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        //组件
        mJlist.addMouseListener(new BaseCustomMouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                long end = System.currentTimeMillis();
                Point point = e.getPoint();
                if( mJlist.getSelectedIndex() == select_index && (end - start) < 1000) {
                    CutUtil.setSysClipboardText(mJlist.getSelectedValue());
                    log.info("copy success");
                    popupMenu.add(new JLabel("<html><body style='width: 200px; color:green;'>复制成功</pre</body></html>"));
                    popupMenu.show(mJlist, point.x, point.y);
                }else {
                    popupMenu = new JPopupMenu();
                    //转义html标签
                    String escapedValue = StringEscapeUtils.escapeHtml4(mJlist.getSelectedValue());
                    //添加你需要显示的内容
                    popupMenu.add(new JLabel("<html><body style='width: 200px;'><pre><code>" + escapedValue
                            + "</code></pre</body></html>"));
                    popupMenu.show(mJlist, point.x + 5, point.y + 10);
                }
                select_index = mJlist.getSelectedIndex();
                start = end;
            }
        });

        jScrollPane = new JScrollPane(mJlist);
        jScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        jScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        jScrollPane.setOpaque(false);
        jScrollPane.getViewport().setOpaque(false);
        jScrollPane.setSize(1000,1000);
        jScrollPane.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentShown(ComponentEvent e) {
                super.componentShown(e);
            }
        });

        //添加默认序号

        mJlist.setCellRenderer(new DefaultListCellRenderer(){
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                Component renderer = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                String text = "[" +(index + 1)+"]" + " " + value.toString();
                if(index % 2 == 0) {
                    ((JLabel) renderer).setBackground(new Color(210,210,210));
                    ((JLabel) renderer).setFocusable(true);
                }
                if (isSelected) {
                    renderer.setBackground(list.getSelectionBackground());
                    renderer.setForeground(list.getSelectionForeground());
                }
                ((JLabel) renderer).setText(text);
                return renderer;
            }
        });
        //添加头panel
        HeaderJPanel headerJPanel = new HeaderJPanel();
        headerJPanel.setMinimumSize(new Dimension(0,20));
        headerJPanel.setMaximumSize(new Dimension(1000,20));
        JPanel jPanel = new JPanel();
        BoxLayout boxLayout = new BoxLayout(jPanel, BoxLayout.Y_AXIS);
        jPanel.setLayout(boxLayout);
        jPanel.add(headerJPanel);
        jPanel.add(jScrollPane);
        cutHelper.setContentPane(jPanel);
        cutHelper.setAlwaysOnTop(true);

        //读取记录
        // 5. 显示窗口，前面创建的信息都在内存中，通过 jf.setVisible(true) 把内存中的窗口显示在屏幕上。
        cutHelper.setVisible(true);
        CutList.getInstance().getListFromFile();
    }

    public static void addListModel(Collection<String> list) {
        list.forEach(str->listModel.add(0,str));
        mJlist.setSelectedIndex(0);
        mJlist.ensureIndexIsVisible(0);
    }


    public static void filterSearchText(String keyword) {
        // 遍历数据模型，设置所有项为隐藏
        log.info("search keyword: "+keyword);
        if(StringUtils.isBlank(keyword)) {
            mJlist.setModel(listModel);
            return;
        }
        searchListModel.clear();
        for (int i = 0; i < listModel.getSize(); i++) {
            String item = listModel.getElementAt(i);
            if (StringUtils.containsIgnoreCase(item, keyword)) {
                searchListModel.addElement(item);
            }
        }
        mJlist.setModel(searchListModel);
        searchFlag = true;
    }

}
