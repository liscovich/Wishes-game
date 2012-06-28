$.extend({
  getUrlVars: function(){
    var vars = [], hash;
    var hashes = window.location.href.slice(window.location.href.indexOf('?') + 1).split('&');
    for(var i = 0; i < hashes.length; i++)
    {
      hash = hashes[i].split('=');
      vars.push(hash[0]);
      vars[hash[0]] = hash[1];
    }
    return vars;
  },
  getUrlVar: function(name){
    return $.getUrlVars()[name];
  }
});
jQuery.preloadImages = function() {
  for ( var i = 0; i < arguments.length; i++) {
    jQuery("<img>").attr("src", arguments[i]);
  }
}
function round_to_2_points(num) {
  var num_string = num.toString();
  var decimal_point_index = num_string.indexOf('.');
  if (decimal_point_index > -1 && decimal_point_index < num_string.length - 2) {
    return num_string.substring(0, decimal_point_index+3);
  } else {
    return num_string;
  }
}