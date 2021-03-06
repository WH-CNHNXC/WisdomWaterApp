package cn.xlmdz.wisdomwaterapp.utils;

import android.graphics.Rect;
import android.view.View;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class VerticalSpacingItemDecoration extends RecyclerView.ItemDecoration {
    private int mSpace;
    private int mEdgeSpace;

    public VerticalSpacingItemDecoration(int mSpace) {
        this.mSpace = mSpace;
    }

    public VerticalSpacingItemDecoration(int mSpace, int EdgeSpace) {
        this.mSpace = mSpace;
        this.mEdgeSpace = EdgeSpace;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        //简单写法--仅仅判断了第一个
        int position = parent.getChildAdapterPosition(view);// item position
        if (position != 0) {
            outRect.set(0, mSpace, 0, 0);
        }

//        RecyclerView.LayoutManager manager = parent.getLayoutManager();
//        int childPosition = parent.getChildAdapterPosition(view);
//        int itemCount = parent.getAdapter().getItemCount();
//        if (manager != null) {
//            if (manager instanceof GridLayoutManager) {
//                setGridOffset(
//                    ((GridLayoutManager) manager).getOrientation(),
//                    ((GridLayoutManager) manager).getSpanCount(),
//                    outRect,
//                    childPosition,
//                    itemCount
//                );
//            } else if (manager instanceof LinearLayoutManager) {
//                setLinearOffset(
//                    ((LinearLayoutManager) manager).getOrientation(),
//                    outRect,
//                    childPosition,
//                    itemCount
//                );
//            }
//        }
    }

    /**
     * 设置LinearLayoutManager 类型的 offset
     *
     * @param orientation   方向
     * @param outRect       padding
     * @param childPosition 在 list 中的 position
     * @param itemCount     list size
     */
    private void setLinearOffset(int orientation, Rect outRect, int childPosition, int itemCount) {
        if (orientation == LinearLayoutManager.HORIZONTAL) {
            if (childPosition == 0) {
                // 第一个要设置PaddingLeft
                outRect.set(mEdgeSpace, 0, mSpace, 0);
            } else if (childPosition == itemCount - 1) {
                // 最后一个设置PaddingRight
                outRect.set(0, 0, mEdgeSpace, 0);
            } else {
                outRect.set(0, 0, mSpace, 0);
            }
        } else {
            if (childPosition == 0) {
                // 第一个要设置PaddingTop
                outRect.set(0, mEdgeSpace, 0, mSpace);
            } else if (childPosition == itemCount - 1) {
                // 最后一个要设置PaddingBottom
                outRect.set(0, 0, 0, mEdgeSpace);
            } else {
                outRect.set(0, 0, 0, mSpace);
            }
        }
    }

    /**
     * 设置GridLayoutManager 类型的 offset
     *
     * @param orientation   方向
     * @param spanCount     个数
     * @param outRect       padding
     * @param childPosition 在 list 中的 position
     * @param itemCount     list size
     */
    private void setGridOffset(int orientation, int spanCount, Rect outRect, int childPosition, int itemCount) {
        float totalSpace = mSpace * (spanCount - 1) + mEdgeSpace * 2;
        float eachSpace = totalSpace / spanCount;
        int column = childPosition % spanCount;
        int row = childPosition / spanCount;

        float left;
        float right;
        float top;
        float bottom;
        if (orientation == GridLayoutManager.VERTICAL) {
            top = 0;
            bottom = mSpace;

            if (childPosition < spanCount) {
                top = mEdgeSpace;
            }

            bottom = getBottom(spanCount, itemCount, row, bottom);

            if (spanCount == 1) {
                left = mEdgeSpace;
                right = left;
            } else {
                left = column * (eachSpace - mEdgeSpace - mEdgeSpace) / (spanCount - 1) + mEdgeSpace;
                right = eachSpace - left;
            }
        } else {
            left = 0;
            right = mSpace;

            if (childPosition < spanCount) {
                left = mEdgeSpace;
            }

            right = getRight(spanCount, itemCount, row, right);

            if (spanCount == 1) {
                top = mEdgeSpace;
                bottom = top;
            } else {
                top = column * (eachSpace - mEdgeSpace - mEdgeSpace) / (spanCount - 1) + mEdgeSpace;
                bottom = eachSpace - top;
            }
        }
        outRect.set((int) left, (int) top, (int) right, (int) bottom);
    }

    private float getBottom(int spanCount, int itemCount, int row, float bottom) {
        if (itemCount % spanCount != 0 && itemCount / spanCount == row ||
                itemCount % spanCount == 0 && itemCount / spanCount == row + 1) bottom = mEdgeSpace;
        return bottom;
    }

    private float getRight(int spanCount, int itemCount, int row, float right) {
        if (itemCount % spanCount != 0 && itemCount / spanCount == row ||
                itemCount % spanCount == 0 && itemCount / spanCount == row + 1) right = mEdgeSpace;
        return right;
    }
}
