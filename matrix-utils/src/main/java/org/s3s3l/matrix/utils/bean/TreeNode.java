package org.s3s3l.matrix.utils.bean;

import java.util.List;

/**
 * <p>
 * </p>
 * ClassName:TreeNode <br>
 * 
 * @author kehw_zwei
 * @version 1.0.0
 * @since JDK 1.8
 */
public interface TreeNode<T extends TreeNode<T>> {

    List<T> getChildren();

    void setChildren(List<T> children);
}
