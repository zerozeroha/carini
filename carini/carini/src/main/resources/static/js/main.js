(function($) {

    var $window = $(window),
        $body = $('body'),
        settings = {
            // Carousels
            carousels: {
                speed: 2, // Speed for smoother movement
                fadeIn: true,
                fadeDelay: 100, // Faster fade-in
                autoScroll: true, // Auto-scroll feature
                autoScrollInterval: 20 // Smaller interval for smoother scrolling
            },
        };

    // Breakpoints.
    breakpoints({
        wide: [ '1281px', '1680px' ],
        normal: [ '961px', '1280px' ],
        narrow: [ '841px', '960px' ],
        narrower: [ '737px', '840px' ],
        mobile: [ null, '736px' ]
    });

    // Play initial animations on page load.
    $window.on('load', function() {
        window.setTimeout(function() {
            $body.removeClass('is-preload');
        }, 100);
    });

    // Dropdowns.
    $('#nav > ul').dropotron({
        mode: 'fade',
        speed: 350,
        noOpenerFade: true,
        alignment: 'center'
    });

    // Scrolly.
    $('.scrolly').scrolly();

    // Nav.

    // Button.
    $(
        '<div id="navButton">' +
        '<a href="#navPanel" class="toggle"></a>' +
        '</div>'
    ).appendTo($body);

    // Panel.
    $(
        '<div id="navPanel">' +
        '<nav>' +
        $('#nav').navList() +
        '</nav>' +
        '</div>'
    ).appendTo($body).panel({
        delay: 500,
        hideOnClick: true,
        hideOnSwipe: true,
        resetScroll: true,
        resetForms: true,
        target: $body,
        visibleClass: 'navPanel-visible'
    });

    // Carousels.
    $('.carousel').each(function() {

        var $t = $(this),
            $reel = $t.children('.reel'),
            $items = $reel.children('article');

        var pos = 0,
            reelWidth,
            itemWidth,
            timerId;

        // Items.
        if (settings.carousels.fadeIn) {

            $items.addClass('loading');

            $t.scrollex({
                mode: 'middle',
                top: '-20vh',
                bottom: '-20vh',
                enter: function() {

                    var timerId,
                        limit = $items.length - Math.ceil($window.width() / itemWidth);

                    timerId = window.setInterval(function() {
                        var x = $items.filter('.loading'),
                            xf = x.first();

                        if (x.length <= limit) {
                            window.clearInterval(timerId);
                            $items.removeClass('loading');
                            return;
                        }

                        xf.removeClass('loading');

                    }, settings.carousels.fadeDelay);

                }
            });

        }

        // Main.
        $t._update = function() {
            pos = 0;
            reelWidth = $reel[0].scrollWidth;
            itemWidth = $items.outerWidth(true); // Get the width of each item including margin
            $t._updatePos();
        };

        $t._updatePos = function() {
            $reel.css('transform', 'translate(' + pos + 'px, 0)');
        };

        // Auto-Scroll
        function startAutoScroll() {
            timerId = setInterval(function() {
                pos -= settings.carousels.speed;
                if (pos <= -reelWidth) {
                    pos = $window.width();
                }
                $t._updatePos();
            }, settings.carousels.autoScrollInterval);
        }

        // Init.
        $window.on('load', function() {
            $t._update();
            startAutoScroll();

            $window.on('resize', function() {
                $t._update();
            }).trigger('resize');
        });

    });

})(jQuery);
