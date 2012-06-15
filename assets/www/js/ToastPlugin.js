/**
 * Created with IntelliJ IDEA.
 * User: DotNEt
 * Date: 12-5-22
 * Time: 下午1:00
 * To change this template use File | Settings | File Templates.
 */
var ToastPlugin = function () {};
ToastPlugin.prototype.makeText = function (showText, succCallBack, failureCallBack) {
    return cordova.exec(succCallBack, failureCallBack, 'ToastPlugin', 'makeText', [showText]);
}
cordova.addConstructor(function () {
    cordova.addPlugin('ToastPlugin', new ToastPlugin());
});