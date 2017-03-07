$(function () {
    $.get('/autoconfig').done(
	    function (result) {
		var addThymeleafBannerConditions = function (mark, values, foo) {
		    var autoconfigView = $('.autoconfig tbody');
		    for (key in values) {
			if (key.startsWith('ThymeleafBanner')) {

			    var conditions = $('<td>').append($('<p>').text(key));
			    var mappingFunction = function (e) {
				return $('<li>').append(
					document.createTextNode(e.condition),
					$('<br/>'),
					document.createTextNode(e.message)
					);
			    };
			    var tryAppendingToConditions = function(v, k) {
				if (v.hasOwnProperty(k) && v[k].length > 0) {
				    conditions.append($('<p>').text(k), $('<ul>').append(v[k].map(mappingFunction)));
				}
			    }
			    

			    if ($.isArray(values[key])) {
				conditions.append($('<ul>').append(values[key].map(mappingFunction)));
			    } else {
				tryAppendingToConditions(values[key], 'matched');
				tryAppendingToConditions(values[key], 'notMatched');
			    }

			    autoconfigView.append(
				    $('<tr>').append(
				    conditions,
				    $('<td>').text(mark)
				    )
				    );
			}
		    }
		};

		addThymeleafBannerConditions('[x]', result.positiveMatches);
		addThymeleafBannerConditions('[ ]', result.negativeMatches, true);
	    }
    );
});