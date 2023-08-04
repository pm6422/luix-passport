/**
 * Filters
 */
angular
    .module('smartcloudserviceApp')
    .filter('words', words)
    .filter('characters', characters)
    .filter('capitalize', capitalize)
    .filter('nullToWhite', nullToWhite);

function words () {
    return function (input, words) {
        if (isNaN(words)) {
            return input;
        }
        if (words <= 0) {
            return '';
        }
        if (input) {
            var inputWords = input.split(/\s+/);
            if (inputWords.length > words) {
                input = inputWords.slice(0, words).join(' ') + '...';
            }
        }
        
        return input;
    }
}
function characters () {
    return function (input, chars, breakOnWord) {
        if (isNaN(chars)) {
            return input;
        }
        if (chars <= 0) {
            return '';
        }
        if (input && input.length > chars) {
            input = input.substring(0, chars);

            if (!breakOnWord) {
                var lastspace = input.lastIndexOf(' ');
                // Get last space
                if (lastspace !== -1) {
                    input = input.substr(0, lastspace);
                }
            } else {
                while (input.charAt(input.length-1) === ' ') {
                    input = input.substr(0, input.length - 1);
                }
            }
            return input + '...';
        }
        return input;
    }
}
function capitalize () {
    return function (input) {
        if (input !== null) {
            input = input.toLowerCase();
        }
        return input.substring(0, 1).toUpperCase() + input.substring(1);
    }
}
function nullToWhite () {
    return function (input) {
        var result = input;
        if (input === undefined || input === null || input === "null") {
            result = "";
        }
        return result;
    };
}