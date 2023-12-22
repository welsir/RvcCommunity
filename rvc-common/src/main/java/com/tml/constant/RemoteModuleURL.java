package com.tml.constant;

/**
 * @Description
 * @Author welsir
 * @Date 2023/12/6 19:13
 */
public class RemoteModuleURL {

    /*
     * 文件模块上传URL
     */
    public static final String UPLOAD_FILE_TO_OSS = "file/oss/upload";
    /*
     * 文件list上传
     **/
    public static final String UPLOAD_FILE_LIST_TO_OSS = "file/oss/upload/list";

    /*
     * 文件模块下载URL
     */
    public static final String DOWNLOAD_FILE_TO_OSS = "/file/oss/download";

    /*
     * 用户模块获取用户信息
     */
    public static final String GET_USERINFO = "/user/one";

    /*
     * 模型模块获取用户点赞的模型列表
     */
    public static final String GET_USER_LIKES_MODELS = "/model/likes";

    /*
     * 模型模块获取用户收藏的模型列表
     */
    public static final String GET_USER_COLLECTION_MODELS = "/model/collection";
}
