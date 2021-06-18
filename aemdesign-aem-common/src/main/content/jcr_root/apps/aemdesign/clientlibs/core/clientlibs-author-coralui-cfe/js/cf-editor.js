//cf-editor.js - additional logic for content fragment editor

(function ($) {
    'use strict';

  $("[data-element$='__asset']").each(function(){
    var name = $(this).attr("data-element").split("__asset")[0];
    if (name) {
      console.log("loading paragraph counter to field [" + name + "].");
      var selector = ".cfm-multieditor:has(input[data-element='" + name + "']) .cfm-multieditor-richtext-editor"
      $(selector).addClass("paragraphcounter");
    }
  })

}(jQuery));
