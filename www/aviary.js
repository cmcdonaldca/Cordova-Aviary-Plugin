/*
 * PhoneGap is available under *either* the terms of the modified BSD license *or* the
 * MIT License (2008). See http://opensource.org/licenses/alphabetical for full text.
 *
 * Colin McDonald - colinmcdonald.ca
 */

var Aviary = (function (gap) {
    function isFunction(f) {
        return typeof f === "function";
    }

    // placeholder and constants
    function Aviary() {}

    /**
     * Display a new browser with the specified URL.
     * This method loads up a new web view in a dialog.
     *
     * @param src           The location of the file to load Aviary with
     * @param options       An object that contains two functions: success and error
     */
    Aviary.showWebPage = function (src, options) {
        gap.exec(options.success, options.error, "Aviary", "show", [src]);
    };

    /**
     * Load Aviary
     */
    gap.addConstructor(function () {
        if (gap.addPlugin) {
            gap.addPlugin("aviary", Aviary);
        } else {
            if (!window.plugins) {
                window.plugins = {};
            }

            window.plugins.aviary = Aviary;
        }
    });

    return Aviary;
})(window.cordova || window.Cordova || window.PhoneGap);