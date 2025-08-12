var exec = require('cordova/exec');

module.exports = {
    start: function (success, error) {
        exec(success, error, 'ProximitySensor', 'start', []);
    },
    stop: function (success, error) {
        exec(success, error, 'ProximitySensor', 'stop', []);
    }
};
