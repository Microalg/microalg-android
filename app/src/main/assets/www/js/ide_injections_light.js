function cleanTransient(text) {
    text = text.replace(/\^J/g,'\n');  // PicoLisp control char
    text = text.replace(/\\"/g,'"');   // unescape double quotes
    text = text.replace(/\n$/,'');     // remove last newline
    if (text.charAt(0) == '"' && text.charAt(text.length-1) == '"') {
        text = text.slice(1, -1);      // remove enclosing quotes
    }
    return text;
}

function stdPrint(text, state) {
    var target = $('#' + state.context.display_elt);
    text = cleanTransient(text);
    if (state.context.type == 'editor') {
        if (target.html() == "&nbsp;" && text != "") {
            target.html("");            // clean the target
        }
        if (typeof Showdown != 'undefined') {
            text = new Showdown.converter().makeHtml(text);
        }
        target.html(target.html() + text);
    }
    if (state.context.type == 'repl') {
        var repl_elt = $('#' + state.context.display_elt);
        if (text !== undefined && text != '' && text != 'NIL') {
            repl_elt.val(repl_elt.val() + "\n" + text);
        }
    }
    if (state.context.type == 'jrepl') {
        state.context.term.echo(text);
    }
}

function stdPrompt() {
    var last_line_displayed = cleanTransient(EMULISP_CORE.eval('*LastStdOut'));
    var user_input = window.prompt(last_line_displayed);
    if (user_input !== null) return user_input;
    else throw new Error("Commande `Demander` annulée.")
}

function ide_action(src) {
    // Init the state and load it with MicroAlg.
    EMULISP_CORE.init();
    EMULISP_CORE.eval(EMULISP_CORE.getFileSync('file:///android_asset/www/microalg.l'));
    // Custom state for a custom display in the page.
    EMULISP_CORE.currentState().context = {type: 'editor', display_elt: 'display'};
    var error_elt = $('#error');
    var display_elt = $('#display');
    display_elt.html('&nbsp;');
    try {
        error_elt.text('');
        EMULISP_CORE.eval(src);
    } catch(e) {
        var link = '<a target="_blank" href="http://microalg.info/doc.html#erreursfrquentes">Voir les erreurs fréquentes.</a>';
        error_elt.html(e.message + ' <span class="malg-freq-error">' + link + '</span>');
    }
 }