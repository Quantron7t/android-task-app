
const defaultToolbarIconColor = '#999999';
const defaultToolbarIconSelectColor = '#040c29';

const slideInElement = document.querySelector('.slide-in');

var QE = {};
QE.isCursorOnEditor = false;
QE.editor = document.getElementById('editor-container');

var quill = new Quill('#editor-container', {
    modules: {
        syntax: true,
        toolbar: false
    },
    placeholder: 'Add task details here. . .',
    theme: 'snow'
});

window.addEventListener('DOMContentLoaded', function () {
    if (quill.hasFocus()) {
        QE.isCursorOnEditor = true;
        slideInElement.classList.remove('hidden');
        slideInElement.classList.add('active');
    }
    else {
        QE.isCursorOnEditor = false;
        slideInElement.classList.remove('active');
        slideInElement.classList.add('hidden');
    }

    var editorContainer = document.querySelector('#editor-container');
    editorContainer.addEventListener('focusin', function (event) {
        var clickedElement = event.target;
        if (QE.isCursorOnEditor) {
            slideInElement.classList.remove('hidden');
            slideInElement.classList.add('active');
        }
    });

    editorContainer.addEventListener('focusout', function (event) {
        var clickedElement = event.target;
        if (!QE.isCursorOnEditor) {
            slideInElement.classList.remove('active');
            slideInElement.classList.add('hidden');
        }
    });
});

// set html content
QE.setHTML = (html) => {
    quill.setContents(JSON.parse(Base64.decode(html)), 'silent');
};

// get html content
QE.getHTML = () => {
    return quill.root.innerHTML;
};

quill.on('selection-change', function (range, oldRange, source) {
    if (range) {
        if (range.length == 0) {
            let f = quill.getFormat(range.index, range.length);
            if (f) updateToolbar(f);
        } else {
            let f = quill.getFormat(range.index, range.length);
            if (f) updateToolbar(f);
        }
    } else {
        console.log('Cursor not in the editor');
    }

    if (quill.hasFocus()) QE.isCursorOnEditor = true;
    else QE.isCursorOnEditor = false;
});

quill.on('text-change', function (delta, oldDelta, source) {
    let f = quill.getFormat();
    if (f) updateToolbar(f);
    //QE.callback();
});

quill.on('editor-change', function (eventName, ...args) {
    if (quill.hasFocus()) QE.isCursorOnEditor = true;
    else QE.isCursorOnEditor = false;
});

// Adjust the editor container height dynamically based on viewport size
function adjustEditorContainerHeight() {
    var footerHeight = document.querySelector('.footer').offsetHeight;
    var editorContainer = document.querySelector('#editor-container');
    editorContainer.style.height = `calc(100% - ${footerHeight}px)`;
}

// Call the adjustEditorContainerHeight function on page load and when the window is resized
window.addEventListener('load', adjustEditorContainerHeight);
window.addEventListener('resize', adjustEditorContainerHeight);

function scrollToEnd(event) {
    quill.focus();
    event.preventDefault();
    event.stopImmediatePropagation();
    var container = document.querySelector('.icon-container');
    var scrollWidth = container.scrollWidth - container.clientWidth;
    container.scrollTo({ left: scrollWidth, behavior: 'smooth' });
}

let superscriptIcon = document.getElementsByClassName('superscript-icon')[0];
let subscriptIcon = document.getElementsByClassName('subscript-icon')[0];
let boldIcon = document.getElementsByClassName('bold-icon')[0];
let italicIcon = document.getElementsByClassName('italic-icon')[0];
let strikethroughIcon = document.getElementsByClassName('strikethrough-icon')[0];
let underlineIcon = document.getElementsByClassName('underline-icon')[0];
let textHighlightIcon = document.getElementsByClassName('text-highlight-icon')[0];
let textColorIcon = document.getElementsByClassName('text-color-icon')[0];
let headerOptionIcon = document.getElementsByClassName('header-levels-options-icon')[0];
let BlockquoteOptionIcon = document.getElementsByClassName('blockquote-icon')[0];
let CodeblockOptionIcon = document.getElementsByClassName('codeblock-icon')[0];

QE.setHeader = function (headerValue, fontIcon) {
    //DO NOT USE quill.getSelection();
    //using getSelection() api closes the keyboard abruptly and weird issues     
    headerOptionIcon.innerHTML = fontIcon;

    quill.focus();
    let currentFormat = quill.getFormat();

    // Check if the current block is a header 2
    if (currentFormat['header'] === headerValue) {
        // Remove header 2 format
        quill.format('header', false);
    } else {
        // Apply header 2 format
        quill.format('header', headerValue);
    }
    tippy.hideAll();
}

QE.setBold = function () {
    let currentFormat = quill.getFormat();
    if (currentFormat['bold']) {
        boldIcon.style.color = defaultToolbarIconColor;
    } else {
        boldIcon.style.color = defaultToolbarIconSelectColor;
    }
    quill.format('bold', !currentFormat['bold']);
    quill.update();
    quill.focus();
}

QE.setItalic = function () {
    let currentFormat = quill.getFormat();
    if (currentFormat['italic']) {
        italicIcon.style.color = defaultToolbarIconColor;
    } else {
        italicIcon.style.color = defaultToolbarIconSelectColor;
    }
    quill.format('italic', !currentFormat['italic']);
    quill.update();
    quill.focus();
}

QE.setStrike = function () {
    //DO NOT USE quill.getSelection();
    //using getSelection() api closes the keyboard abruptly and weird issues 
    let currentFormat = quill.getFormat();
    // Check if the current selection has underline format 
    if (currentFormat['strike']) {
        strikethroughIcon.style.color = defaultToolbarIconColor;
    } else {
        strikethroughIcon.style.color = defaultToolbarIconSelectColor;
    }
    quill.format('strike', !quill.getFormat()['strike']);
    quill.update();
    quill.focus();
}

QE.setUnderline = function () {
    //DO NOT USE quill.getSelection();
    //using getSelection() api closes the keyboard abruptly and weird issues   
    let currentFormat = quill.getFormat();
    // Check if the current selection has underline format 
    if (currentFormat['underline']) {
        underlineIcon.style.color = defaultToolbarIconColor;
    } else {
        underlineIcon.style.color = defaultToolbarIconSelectColor;
    }

    quill.format('underline', !quill.getFormat()['underline']);
    quill.update();
    quill.focus();
}

QE.setSuperscript = function () {
    subscriptIcon.style.color = defaultToolbarIconColor;
    superscriptIcon.style.color = defaultToolbarIconColor;
    let currentFormat = quill.getFormat();

    // Check if the current block is a code block
    if (currentFormat && currentFormat['script'] === 'super') {
        superscriptIcon.style.color = defaultToolbarIconColor;
        //Do not apply delay - It causes soft keyboard to duck        
        quill.format('script', false, 'user');
    } else {
        // Apply code block format        
        superscriptIcon.style.color = defaultToolbarIconSelectColor;
        //Do not apply timeout delay - It causes soft keyboard to duck
        quill.format('script', 'sup', 'user');
    }

    quill.update();
    quill.focus();
}

QE.setSubscript = function () {
    subscriptIcon.style.color = defaultToolbarIconColor;
    superscriptIcon.style.color = defaultToolbarIconColor;
    let currentFormat = quill.getFormat();

    // Check if the current block is a code block
    if (currentFormat && currentFormat['script'] === 'sub') {
        subscriptIcon.style.color = defaultToolbarIconColor;
        //Do not apply delay - It causes soft keyboard to duck
        quill.format('script', false, 'user');
    } else {
        // Apply code block format        
        subscriptIcon.style.color = defaultToolbarIconSelectColor;
        //Do not apply delay - It causes soft keyboard to duck
        quill.format('script', 'sub', 'user');
    }

    quill.update();
    quill.focus();
}

QE.clearSelection = function () {
    //https://github.com/quilljs/quill/issues/388#issuecomment-109417733
    quill.focus();
    let range = quill.getSelection();
    quill.removeFormat(range.index, range.length, 'user');
}

QE.setCodeBlock = function () {
    let currentFormat = quill.getFormat();
    // Check if the current block is a code block
    if (currentFormat['code-block'] === true) {
        // Remove code block format
        CodeblockOptionIcon.style.color = defaultToolbarIconColor;
        quill.format('code-block', false, 'user');
    } else {
        // Apply code block format
        CodeblockOptionIcon.style.color = defaultToolbarIconSelectColor;
        quill.format('code-block', true, 'user');
    }
}

QE.setBlockQuote = function () {
    let currentFormat = quill.getFormat();
    // Check if the selection is having blockquote format
    if (currentFormat['blockquote'] === true) {
        // Remove blockquote format
        BlockquoteOptionIcon.style.color = defaultToolbarIconColor;
        quill.format('blockquote', false, 'user');
    } else {
        // Apply blockquote format
        BlockquoteOptionIcon.style.color = defaultToolbarIconSelectColor;
        quill.format('blockquote', true, 'user');
    }
}

QE.setTextColor = function (color) {
    let format = quill.getFormat();
    // Check if the selection is having text color
    if (format['color'] === color) {
        // Remove text color
        textColorIcon.style.color = defaultToolbarIconColor;
        quill.format('color', false, 'user');
    } else {
        // Apply text color
        textColorIcon.style.color = color;
        quill.format('color', color, 'user');
    }
    tippy.hideAll(); //hides regardless any state and doesnt require direct reference of tippy created
}

QE.setHighlightColor = function (color) {
    let format = quill.getFormat();
    // Check if the selection is having text color
    if (format['background'] === color) {
        // Remove text color
        textHighlightIcon.style.color = defaultToolbarIconColor;
        quill.format('background', false);
    } else {
        // Apply text color
        textHighlightIcon.style.color = color;
        quill.format('background', color);
    }
    tippy.hideAll(); //hides regardless any state and doesnt require direct reference of tippy created
}

QE.setCheckbox = function () {
    var range = quill.getSelection();
    if (range) {
        var formats = quill.getFormat(range);
        var listFormat = formats.list;

        if (listFormat === 'checked' || listFormat === 'unchecked') {
            quill.format('list', false);
            quill.format('checked', false);
            quill.format('unchecked', false);
        } else {
            quill.format('list', 'unchecked');
        }
    }
}

const tippyTextColor = document.getElementById('tippy-text-color-options');

tippy('#textColorOptions', {
    content: tippyTextColor,
    trigger: 'click',
    interactive: true,
    animation: 'scale-subtle',
    arrow: false,
    onCreate(instance) {
        quill.focus();
    },
    onMount(instance) {
        quill.focus();
    },
    onTrigger(instance, event) {
        quill.focus();
    },
    onShown(instance) {
        quill.focus();
    },
    onShow(instance) {
        quill.focus();
    }
});

const tippyHighlightColor = document.getElementById('tippy-highlight-color-options');

tippy('#textHighlightOptions', {
    content: tippyHighlightColor,
    trigger: 'click',
    interactive: true,
    animation: 'scale-subtle',
    arrow: false,
    onCreate(instance) {
        quill.focus();
    },
    onMount(instance) {
        quill.focus();
    },
    onTrigger(instance, event) {
        quill.focus();
    },
    onShown(instance) {
        quill.focus();
    },
    onShow(instance) {
        quill.focus();
    }
});

const tippyHeaderLevels = document.getElementById('tippy-header-levels');

tippy('#headerLevelsOptions', {
    content: tippyHeaderLevels,
    trigger: 'click',
    interactive: true,
    animation: 'scale-subtle',
    arrow: false,
    onCreate(instance) {
        quill.focus();
    },
    onMount(instance) {
        quill.focus();
    },
    onTrigger(instance, event) {
        quill.focus();
    },
    onShown(instance) {
        quill.focus();
    },
    onShow(instance) {
        quill.focus();
    }
});

function resetAllOptions() {
    boldIcon.style.color = defaultToolbarIconColor;
    italicIcon.style.color = defaultToolbarIconColor;
    strikethroughIcon.style.color = defaultToolbarIconColor;
    underlineIcon.style.color = defaultToolbarIconColor;
    superscriptIcon.style.color = defaultToolbarIconColor;
    subscriptIcon.style.color = defaultToolbarIconColor;
    textHighlightIcon.style.color = defaultToolbarIconColor;
    textColorIcon.style.color = defaultToolbarIconColor;
    BlockquoteOptionIcon.style.color = defaultToolbarIconColor;
    CodeblockOptionIcon.style.color = defaultToolbarIconColor;
    subscriptIcon.style.color = defaultToolbarIconColor;
    superscriptIcon.style.color = defaultToolbarIconColor;
}

function updateToolbar(formats) {
    resetAllOptions();
    if (formats['bold']) boldIcon.style.color = defaultToolbarIconSelectColor;
    if (formats['italic']) italicIcon.style.color = defaultToolbarIconSelectColor;
    if (formats['strike']) strikethroughIcon.style.color = defaultToolbarIconSelectColor;
    if (formats['underline']) underlineIcon.style.color = defaultToolbarIconSelectColor;
    if (formats['blockquote']) BlockquoteOptionIcon.style.color = defaultToolbarIconSelectColor;
    if (formats['code-block']) CodeblockOptionIcon.style.color = defaultToolbarIconSelectColor;

    if (formats['script'] && formats['script'] === 'sub') subscriptIcon.style.color = defaultToolbarIconSelectColor;
    if (formats['script'] && formats['script'] === 'super') superscriptIcon.style.color = defaultToolbarIconSelectColor;

    if (formats['background']) textHighlightIcon.style.color = formats['background'];
    if (formats['color']) textColorIcon.style.color = formats['color'];

    if (formats['header']) headerOptionIcon.innerHTML = `format_h${formats['header']}`
    else headerOptionIcon.innerHTML = 'match_case'
}

/**
 * Checkbox resolution START
 * Addressing checkbox check/uncheck issue on android  
 * https://github.com/quilljs/quill/issues/2031
*/
const List = Quill.import("formats/list");

class BetterList extends List {
    constructor(el) {
        super(el);

        const isCheckList = el.hasAttribute("data-checked");
        el.addEventListener("touchstart", (e) => {
            if (!isCheckList) {
                return;
            }
            e.preventDefault();
        });
    }
}

Quill.register("formats/lists", BetterList);
/**
 * Checkbox resolution END
*/

QE.callback = function () {
    //let data = btoa(encodeURIComponent(JSON.stringify(quill.getContents())));
    let quillContents = quill.getContents();
    console.log(JSON.stringify(quillContents));
    let data = Base64.encode((JSON.stringify(quillContents)));
    window.location.href = "re-callback://" + encodeURIComponent(data);
}

QE.editor.addEventListener("keyup", function (e) {
    let f = quill.getFormat();
    updateToolbar(f);//best place to track changes of applied foramts while typing 
    QE.callback();
});

QE.editor.addEventListener('touchstart', function (e) {
    QE.callback();
});

// QE.editor.addEventListener('touchmove', function (e) {
//     QE.callback();
// });

// QE.editor.addEventListener('touchend', function (e) {
//     QE.callback();
// });

function handleOnScroll(event) {
    tippy.hideAll();
    const scrollIndicator = document.getElementsByClassName('animated-scroll-indicator')[0];
    const container = event.target;
    if (container.scrollLeft + container.offsetWidth >= container.scrollWidth) {
        scrollIndicator.classList.add('animated-scroll-indicator-hidden');
    }
    else {
        scrollIndicator.classList.remove('animated-scroll-indicator-hidden');
    }
}