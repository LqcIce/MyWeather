package edu.hrbeu.myweather;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.Scroller;
import android.view.GestureDetector;


public class SlideMenu extends ViewGroup {

        private Scroller scroller;//����ģ������
        private final int MENU = 0;//�����
        private final int MAIN = 1;//������
        private int currentScreen = MENU;//��ǰ���棬Ĭ��Ϊ������
        private int startx;//��ָ����ʱ����һ���������Ļ���Ե�ľ���
        //�����������Ĺ��캯�������Բ����ļ���ʹ��
        public SlideMenu(Context context, AttributeSet attrs) {
                super(context, attrs);
                scroller = new Scroller(context);//��ʼ��
        }

        //��Ϊ����Զ���ؼ��Ǽ̳е�ViewGroup  �ڲ����ļ����ְ����������Ӳ���
        //�������ʾ��ʱ����Ҫ�Ȳ�����Ȼ���ڲ��֣�Ȼ�������ʾ����
        //�÷������ǲ����Ӳ��ֵķ���
        @Override
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                // TODO Auto-generated method stub
                super.onMeasure(widthMeasureSpec, heightMeasureSpec);
                
                View menu = getChildAt(0);//�ҵ������
                menu.measure(menu.getLayoutParams().width, heightMeasureSpec);//����������һ�ǿ�ȣ��������ǻ���Ӳ����ļ���д�����Ǹ����
                View main = getChildAt(1);//���������
                main.measure(widthMeasureSpec, heightMeasureSpec);//����������Ϊ�븸���ֿ��һ��
        }
        
        //�������Ӳ��ֽ��в���
        @Override
        protected void onLayout(boolean changed, int l, int t, int r, int b) {
                // TODO Auto-generated method stub
                View menu = getChildAt(0);//�õ������
                menu.layout(-menu.getLayoutParams().width, t, 0, b);//���ÿؼ����ıߣ������Ĭ������Ϊ��ʾ����Ļ�����Ե��
                View main = getChildAt(1);//�õ�������
                main.layout(l, t, r, b);//������Ĭ�������Ļ
        }

        //�����¼�
        @Override
        public boolean onTouchEvent(MotionEvent event) {
                switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                        startx = (int) event.getX();//��¼��ָ����ʱ���㵽��Ļ��ߵľ���
                        break;
                case MotionEvent.ACTION_MOVE:
                        int movex = (int) event.getX();//�ƶ�����ָ�㵽��Ļ��ߵľ���
                        int diffx = startx - movex;//��Ļ��ߵ�ƫ����
                        int newscrollx =getScrollX()+diffx;//ƫ�ƺ�
                        if(newscrollx>0){
                                scrollTo(0, 0);//�����Ļ��߳�������������ߣ���ô����Ļ������������غ�
                        }else if(newscrollx<-getChildAt(0).getWidth()){
                                scrollTo(-getChildAt(0).getWidth(), 0);//�����Ļ��߳����˲������ߣ���ô����Ļ�������������غ�
                        }
                        scrollBy(diffx, 0);//����ƫ��
                        startx = movex;
                        break;
                case MotionEvent.ACTION_UP:
                        int scrollx = getScrollX();//��Ļ��߾�����������ߵľ��룬��Ļ�������������ߵ���ߣ�Ϊ��ֵ
                        if(scrollx>-getChildAt(0).getWidth()/2){
                                currentScreen = MAIN;//�϶���Ļ�����������һ��ʱ�����֣���ʾ������
                                switchScreen();
                        }else if(scrollx<-getChildAt(0).getWidth()/2){
                                currentScreen = MENU;//�϶���Ļ�����˲������һ�㣬���֣���ʾ�����
                                switchScreen();
                        }
                        break;

                default:
                        break;
                }
                return true;
        }
        
        //�л���ʾ�������������
        private void switchScreen() {
                int dx = 0 ;
                int startX = getScrollX();//�����Ļ��߾�����������ߵľ���
                if(currentScreen == MAIN){//�л���������
                        dx = 0 - getScrollX();//Ŀ���ǽ���Ļ���������������غ�
                }else if(currentScreen == MENU){
                        dx = -getChildAt(0).getWidth()-getScrollX();//Ŀ���ǽ���Ļ���������������غ�
                }
                //ģ�����ݣ��÷�������������ȥִ�У�ֻ��ģ��
                scroller.startScroll(startX, 0, dx, 0, Math.abs(dx)*5);
                invalidate();//����computeScroll()
        }
        
        //invalidate()�����յĵ��÷�������computeScroll()   �����Ҫ��д�÷���
        @Override
        public void computeScroll() {
                if(scroller.computeScrollOffset()){//������ڽ�������ģ��
                        scrollTo(scroller.getCurrX(), 0);//getCurrX()���������ǣ����ģ������ʱ���ƶ�·���ĵ�
                        invalidate();//ֻҪ�ڽ�������ģ�⣬��ô�ͼ�������computeScroll()�����������ڵݹ�
                }
        }
        //�жϵ�ǰ��ʾ���ǲ��ǲ����
        public boolean isMenuShow() {
                // TODO Auto-generated method stub
                return currentScreen == MENU;
        }

        //���ز����
        public void hideMenu() {
                // TODO Auto-generated method stub
                currentScreen = MAIN;
                switchScreen();
        }

        //��ʾ�����
        public void showMenu() {
                // TODO Auto-generated method stub
                currentScreen = MENU;
                switchScreen();
        }

        
}

