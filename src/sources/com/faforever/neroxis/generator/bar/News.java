package com.faforever.neroxis.generator.bar;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.Window;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.io.ByteArrayInputStream;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.runtime.ObjectMethods;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Supplier;
import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JViewport;
import javax.swing.Scrollable;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

/* JADX INFO: loaded from: app_classes.zip:com/faforever/neroxis/generator/bar/News.class */
final class News {
    private static final String SITE = "https://www.beyondallreason.info";
    private static final String DISCORD = "https://discord.gg/EXAQBeseS";
    private static final Color TILE = new Color(34, 37, 45);
    private static final Color TILE_HOVER = new Color(42, 46, 56);
    private static final Color HEAD = new Color(44, 48, 58);
    private static final Color DEEP = new Color(26, 28, 35);
    private static final Color LIGHT_TEXT = new Color(196, 201, 211);
    private static final int GAP = 18;
    private final Supplier<String> mapServiceUrl;
    private final Runnable openPrime;
    private final Runnable reportBug;
    private final JPanel page = new Page();
    private final Hero hero = new Hero(this);
    private final Carousel carousel = new Carousel(this);
    private final JPanel bottom = new JPanel(new GridBagLayout());
    private final JPanel side = new JPanel(new BorderLayout(0, 0));
    private final Map<String, BufferedImage> pictures = new ConcurrentHashMap();
    private final Map<String, Boolean> asked = new ConcurrentHashMap();
    private final ExecutorService loader = Executors.newFixedThreadPool(2, runnable -> {
        Thread thread = new Thread(runnable, "news-pictures");
        thread.setDaemon(true);
        return thread;
    });
    private BufferedImage logo;
    private volatile boolean loading;
    private long loadedAt;

    /* JADX INFO: loaded from: app_classes.zip:com/faforever/neroxis/generator/bar/News$Post.class */
    static final class Post extends Record {
        private final String date;
        private final String title;
        private final String text;
        private final String image;
        private final String link;
        private final boolean featured;

        Post(String date, String title, String text, String image, String link, boolean featured) {
            this.date = date;
            this.title = title;
            this.text = text;
            this.image = image;
            this.link = link;
            this.featured = featured;
        }

        @Override // java.lang.Record
        public final String toString() {
            return (String) ObjectMethods.bootstrap(MethodHandles.lookup(), "toString", MethodType.methodType(String.class, Post.class), Post.class, "date;title;text;image;link;featured", "FIELD:Lcom/faforever/neroxis/generator/bar/News$Post;->date:Ljava/lang/String;", "FIELD:Lcom/faforever/neroxis/generator/bar/News$Post;->title:Ljava/lang/String;", "FIELD:Lcom/faforever/neroxis/generator/bar/News$Post;->text:Ljava/lang/String;", "FIELD:Lcom/faforever/neroxis/generator/bar/News$Post;->image:Ljava/lang/String;", "FIELD:Lcom/faforever/neroxis/generator/bar/News$Post;->link:Ljava/lang/String;", "FIELD:Lcom/faforever/neroxis/generator/bar/News$Post;->featured:Z").dynamicInvoker().invoke(this) /* invoke-custom */;
        }

        @Override // java.lang.Record
        public final int hashCode() {
            return (int) ObjectMethods.bootstrap(MethodHandles.lookup(), "hashCode", MethodType.methodType(Integer.TYPE, Post.class), Post.class, "date;title;text;image;link;featured", "FIELD:Lcom/faforever/neroxis/generator/bar/News$Post;->date:Ljava/lang/String;", "FIELD:Lcom/faforever/neroxis/generator/bar/News$Post;->title:Ljava/lang/String;", "FIELD:Lcom/faforever/neroxis/generator/bar/News$Post;->text:Ljava/lang/String;", "FIELD:Lcom/faforever/neroxis/generator/bar/News$Post;->image:Ljava/lang/String;", "FIELD:Lcom/faforever/neroxis/generator/bar/News$Post;->link:Ljava/lang/String;", "FIELD:Lcom/faforever/neroxis/generator/bar/News$Post;->featured:Z").dynamicInvoker().invoke(this) /* invoke-custom */;
        }

        @Override // java.lang.Record
        public final boolean equals(Object o) {
            return (boolean) ObjectMethods.bootstrap(MethodHandles.lookup(), "equals", MethodType.methodType(Boolean.TYPE, Post.class, Object.class), Post.class, "date;title;text;image;link;featured", "FIELD:Lcom/faforever/neroxis/generator/bar/News$Post;->date:Ljava/lang/String;", "FIELD:Lcom/faforever/neroxis/generator/bar/News$Post;->title:Ljava/lang/String;", "FIELD:Lcom/faforever/neroxis/generator/bar/News$Post;->text:Ljava/lang/String;", "FIELD:Lcom/faforever/neroxis/generator/bar/News$Post;->image:Ljava/lang/String;", "FIELD:Lcom/faforever/neroxis/generator/bar/News$Post;->link:Ljava/lang/String;", "FIELD:Lcom/faforever/neroxis/generator/bar/News$Post;->featured:Z").dynamicInvoker().invoke(this, o) /* invoke-custom */;
        }

        public String date() {
            return this.date;
        }

        public String title() {
            return this.title;
        }

        public String text() {
            return this.text;
        }

        public String image() {
            return this.image;
        }

        public String link() {
            return this.link;
        }

        public boolean featured() {
            return this.featured;
        }
    }

    /* JADX INFO: loaded from: app_classes.zip:com/faforever/neroxis/generator/bar/News$Link.class */
    static final class Link extends Record {
        private final String text;
        private final String url;
        private final String action;
        private final Color colour;

        Link(String text, String url, String action, Color colour) {
            this.text = text;
            this.url = url;
            this.action = action;
            this.colour = colour;
        }

        @Override // java.lang.Record
        public final String toString() {
            return (String) ObjectMethods.bootstrap(MethodHandles.lookup(), "toString", MethodType.methodType(String.class, Link.class), Link.class, "text;url;action;colour", "FIELD:Lcom/faforever/neroxis/generator/bar/News$Link;->text:Ljava/lang/String;", "FIELD:Lcom/faforever/neroxis/generator/bar/News$Link;->url:Ljava/lang/String;", "FIELD:Lcom/faforever/neroxis/generator/bar/News$Link;->action:Ljava/lang/String;", "FIELD:Lcom/faforever/neroxis/generator/bar/News$Link;->colour:Ljava/awt/Color;").dynamicInvoker().invoke(this) /* invoke-custom */;
        }

        @Override // java.lang.Record
        public final int hashCode() {
            return (int) ObjectMethods.bootstrap(MethodHandles.lookup(), "hashCode", MethodType.methodType(Integer.TYPE, Link.class), Link.class, "text;url;action;colour", "FIELD:Lcom/faforever/neroxis/generator/bar/News$Link;->text:Ljava/lang/String;", "FIELD:Lcom/faforever/neroxis/generator/bar/News$Link;->url:Ljava/lang/String;", "FIELD:Lcom/faforever/neroxis/generator/bar/News$Link;->action:Ljava/lang/String;", "FIELD:Lcom/faforever/neroxis/generator/bar/News$Link;->colour:Ljava/awt/Color;").dynamicInvoker().invoke(this) /* invoke-custom */;
        }

        @Override // java.lang.Record
        public final boolean equals(Object o) {
            return (boolean) ObjectMethods.bootstrap(MethodHandles.lookup(), "equals", MethodType.methodType(Boolean.TYPE, Link.class, Object.class), Link.class, "text;url;action;colour", "FIELD:Lcom/faforever/neroxis/generator/bar/News$Link;->text:Ljava/lang/String;", "FIELD:Lcom/faforever/neroxis/generator/bar/News$Link;->url:Ljava/lang/String;", "FIELD:Lcom/faforever/neroxis/generator/bar/News$Link;->action:Ljava/lang/String;", "FIELD:Lcom/faforever/neroxis/generator/bar/News$Link;->colour:Ljava/awt/Color;").dynamicInvoker().invoke(this, o) /* invoke-custom */;
        }

        public String text() {
            return this.text;
        }

        public String url() {
            return this.url;
        }

        public String action() {
            return this.action;
        }

        public Color colour() {
            return this.colour;
        }
    }

    /* JADX INFO: loaded from: app_classes.zip:com/faforever/neroxis/generator/bar/News$Column.class */
    static final class Column extends Record {
        private final String title;
        private final List<Link> links;

        Column(String title, List<Link> links) {
            this.title = title;
            this.links = links;
        }

        @Override // java.lang.Record
        public final String toString() {
            return (String) ObjectMethods.bootstrap(MethodHandles.lookup(), "toString", MethodType.methodType(String.class, Column.class), Column.class, "title;links", "FIELD:Lcom/faforever/neroxis/generator/bar/News$Column;->title:Ljava/lang/String;", "FIELD:Lcom/faforever/neroxis/generator/bar/News$Column;->links:Ljava/util/List;").dynamicInvoker().invoke(this) /* invoke-custom */;
        }

        @Override // java.lang.Record
        public final int hashCode() {
            return (int) ObjectMethods.bootstrap(MethodHandles.lookup(), "hashCode", MethodType.methodType(Integer.TYPE, Column.class), Column.class, "title;links", "FIELD:Lcom/faforever/neroxis/generator/bar/News$Column;->title:Ljava/lang/String;", "FIELD:Lcom/faforever/neroxis/generator/bar/News$Column;->links:Ljava/util/List;").dynamicInvoker().invoke(this) /* invoke-custom */;
        }

        @Override // java.lang.Record
        public final boolean equals(Object o) {
            return (boolean) ObjectMethods.bootstrap(MethodHandles.lookup(), "equals", MethodType.methodType(Boolean.TYPE, Column.class, Object.class), Column.class, "title;links", "FIELD:Lcom/faforever/neroxis/generator/bar/News$Column;->title:Ljava/lang/String;", "FIELD:Lcom/faforever/neroxis/generator/bar/News$Column;->links:Ljava/util/List;").dynamicInvoker().invoke(this, o) /* invoke-custom */;
        }

        public String title() {
            return this.title;
        }

        public List<Link> links() {
            return this.links;
        }
    }

    /* JADX INFO: loaded from: app_classes.zip:com/faforever/neroxis/generator/bar/News$Event.class */
    static final class Event extends Record {
        private final String date;
        private final String title;
        private final String link;

        Event(String date, String title, String link) {
            this.date = date;
            this.title = title;
            this.link = link;
        }

        @Override // java.lang.Record
        public final String toString() {
            return (String) ObjectMethods.bootstrap(MethodHandles.lookup(), "toString", MethodType.methodType(String.class, Event.class), Event.class, "date;title;link", "FIELD:Lcom/faforever/neroxis/generator/bar/News$Event;->date:Ljava/lang/String;", "FIELD:Lcom/faforever/neroxis/generator/bar/News$Event;->title:Ljava/lang/String;", "FIELD:Lcom/faforever/neroxis/generator/bar/News$Event;->link:Ljava/lang/String;").dynamicInvoker().invoke(this) /* invoke-custom */;
        }

        @Override // java.lang.Record
        public final int hashCode() {
            return (int) ObjectMethods.bootstrap(MethodHandles.lookup(), "hashCode", MethodType.methodType(Integer.TYPE, Event.class), Event.class, "date;title;link", "FIELD:Lcom/faforever/neroxis/generator/bar/News$Event;->date:Ljava/lang/String;", "FIELD:Lcom/faforever/neroxis/generator/bar/News$Event;->title:Ljava/lang/String;", "FIELD:Lcom/faforever/neroxis/generator/bar/News$Event;->link:Ljava/lang/String;").dynamicInvoker().invoke(this) /* invoke-custom */;
        }

        @Override // java.lang.Record
        public final boolean equals(Object o) {
            return (boolean) ObjectMethods.bootstrap(MethodHandles.lookup(), "equals", MethodType.methodType(Boolean.TYPE, Event.class, Object.class), Event.class, "date;title;link", "FIELD:Lcom/faforever/neroxis/generator/bar/News$Event;->date:Ljava/lang/String;", "FIELD:Lcom/faforever/neroxis/generator/bar/News$Event;->title:Ljava/lang/String;", "FIELD:Lcom/faforever/neroxis/generator/bar/News$Event;->link:Ljava/lang/String;").dynamicInvoker().invoke(this, o) /* invoke-custom */;
        }

        public String date() {
            return this.date;
        }

        public String title() {
            return this.title;
        }

        public String link() {
            return this.link;
        }
    }

    News(Supplier<String> mapServiceUrl, Runnable openPrime, Runnable reportBug) {
        this.mapServiceUrl = mapServiceUrl;
        this.openPrime = openPrime;
        this.reportBug = reportBug;
        try {
            URL resource = BarLauncher.class.getResource("barmapstudio.png");
            this.logo = resource != null ? ImageIO.read(resource) : null;
        } catch (Exception e) {
            this.logo = null;
        }
    }

    JComponent view() {
        JPanel view = new BarLauncher.BackgroundPanel(new BorderLayout(0, 0));
        this.page.setOpaque(false);
        this.page.setBorder(BorderFactory.createEmptyBorder(20, 24, 20, 24));
        this.side.setOpaque(false);
        this.bottom.setOpaque(false);
        GridBagConstraints at = new GridBagConstraints();
        at.fill = 1;
        at.gridx = 0;
        at.gridy = 0;
        at.weightx = 1.0d;
        at.insets = new Insets(0, 0, 18, 18);
        this.hero.setPreferredSize(new Dimension(700, 250));
        this.hero.setMinimumSize(new Dimension(300, 250));
        this.page.add(this.hero, at);
        at.gridx = 1;
        at.weightx = 0.0d;
        at.insets = new Insets(0, 0, 18, 0);
        this.side.setPreferredSize(new Dimension(340, 250));
        this.side.setMinimumSize(new Dimension(280, 250));
        this.page.add(this.side, at);
        at.gridx = 0;
        at.gridy = 1;
        at.gridwidth = 2;
        at.weightx = 1.0d;
        this.carousel.setPreferredSize(new Dimension(900, 350));
        this.carousel.setMinimumSize(new Dimension(400, 350));
        this.page.add(this.carousel, at);
        at.gridy = 2;
        at.insets = new Insets(0, 0, 0, 0);
        this.page.add(this.bottom, at);
        at.gridy = 3;
        at.weighty = 1.0d;
        JPanel rest = new JPanel();
        rest.setOpaque(false);
        this.page.add(rest, at);
        showExtras(defaultExtras());
        JScrollPane scroll = new JScrollPane(this.page);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.setHorizontalScrollBarPolicy(31);
        scroll.getVerticalScrollBar().setUnitIncrement(24);
        view.add(scroll, "Center");
        return view;
    }

    /* JADX INFO: loaded from: app_classes.zip:com/faforever/neroxis/generator/bar/News$Page.class */
    private static final class Page extends JPanel implements Scrollable {
        Page() {
            super(new GridBagLayout());
        }

        public Dimension getPreferredScrollableViewportSize() {
            return getPreferredSize();
        }

        public int getScrollableUnitIncrement(Rectangle visible, int orientation, int direction) {
            return 24;
        }

        public int getScrollableBlockIncrement(Rectangle visible, int orientation, int direction) {
            return Math.max(24, visible.height - 48);
        }

        public boolean getScrollableTracksViewportWidth() {
            return true;
        }

        public boolean getScrollableTracksViewportHeight() {
            JViewport parent = getParent();
            if (parent instanceof JViewport) {
                JViewport viewport = parent;
                if (viewport.getHeight() > getPreferredSize().height) {
                    return true;
                }
            }
            return false;
        }
    }

    private String base() {
        return this.mapServiceUrl.get().replaceAll("/find/?$", "");
    }

    void refresh() {
        if (this.loading || System.currentTimeMillis() - this.loadedAt < 60000) {
            return;
        }
        this.loading = true;
        String base = base();
        Thread thread = new Thread(() -> {
            List<Post> loaded = null;
            String problem = null;
            Extras extras = null;
            try {
                HttpResponse<String> response = get(base + "/news.json");
                if (response.statusCode() == 200) {
                    Object patt0$temp = MiniJson.parse((String) response.body());
                    if (patt0$temp instanceof List) {
                        List<?> entries = (List) patt0$temp;
                        loaded = parse(entries);
                    } else {
                        problem = "The news could not be loaded (HTTP " + response.statusCode() + ").";
                    }
                } else {
                    problem = "The news could not be loaded (HTTP " + response.statusCode() + ").";
                }
            } catch (Exception e) {
                problem = "The news could not be loaded: " + e.getMessage();
            }
            try {
                HttpResponse<String> response2 = get(base + "/news-page.json");
                if (response2.statusCode() == 200) {
                    Object patt0$temp2 = MiniJson.parse((String) response2.body());
                    if (patt0$temp2 instanceof Map) {
                        Map<?, ?> map = (Map) patt0$temp2;
                        extras = extras(map);
                    }
                }
            } catch (Exception e2) {
            }
            List<Post> result = loaded;
            String message = problem;
            Extras page = extras;
            SwingUtilities.invokeLater(() -> {
                this.loading = false;
                if (page != null) {
                    showExtras(page);
                }
                if (result != null) {
                    this.loadedAt = System.currentTimeMillis();
                    show(result);
                } else {
                    this.hero.status = message != null ? message : "The news could not be loaded.";
                    this.hero.repaint();
                }
            });
        }, "news");
        thread.setDaemon(true);
        thread.start();
    }

    private static HttpResponse<String> get(String url) throws Exception {
        return HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(6L)).followRedirects(HttpClient.Redirect.NORMAL).build().send(HttpRequest.newBuilder(URI.create(url)).timeout(Duration.ofSeconds(10L)).build(), HttpResponse.BodyHandlers.ofString());
    }

    static List<Post> parse(List<?> entries) {
        List<Post> result = new ArrayList<>();
        for (Object entry : entries) {
            if (entry instanceof Map) {
                Map<?, ?> map = (Map) entry;
                result.add(new Post(text(map.get("date")), text(map.get("title")), text(map.get("text")), text(map.get("image")), text(map.get("link")), Boolean.TRUE.equals(map.get("featured"))));
            }
        }
        return result;
    }

    /* JADX INFO: loaded from: app_classes.zip:com/faforever/neroxis/generator/bar/News$Extras.class */
    static final class Extras extends Record {
        private final List<Link> social;
        private final List<Column> columns;
        private final List<Event> events;

        Extras(List<Link> social, List<Column> columns, List<Event> events) {
            this.social = social;
            this.columns = columns;
            this.events = events;
        }

        @Override // java.lang.Record
        public final String toString() {
            return (String) ObjectMethods.bootstrap(MethodHandles.lookup(), "toString", MethodType.methodType(String.class, Extras.class), Extras.class, "social;columns;events", "FIELD:Lcom/faforever/neroxis/generator/bar/News$Extras;->social:Ljava/util/List;", "FIELD:Lcom/faforever/neroxis/generator/bar/News$Extras;->columns:Ljava/util/List;", "FIELD:Lcom/faforever/neroxis/generator/bar/News$Extras;->events:Ljava/util/List;").dynamicInvoker().invoke(this) /* invoke-custom */;
        }

        @Override // java.lang.Record
        public final int hashCode() {
            return (int) ObjectMethods.bootstrap(MethodHandles.lookup(), "hashCode", MethodType.methodType(Integer.TYPE, Extras.class), Extras.class, "social;columns;events", "FIELD:Lcom/faforever/neroxis/generator/bar/News$Extras;->social:Ljava/util/List;", "FIELD:Lcom/faforever/neroxis/generator/bar/News$Extras;->columns:Ljava/util/List;", "FIELD:Lcom/faforever/neroxis/generator/bar/News$Extras;->events:Ljava/util/List;").dynamicInvoker().invoke(this) /* invoke-custom */;
        }

        @Override // java.lang.Record
        public final boolean equals(Object o) {
            return (boolean) ObjectMethods.bootstrap(MethodHandles.lookup(), "equals", MethodType.methodType(Boolean.TYPE, Extras.class, Object.class), Extras.class, "social;columns;events", "FIELD:Lcom/faforever/neroxis/generator/bar/News$Extras;->social:Ljava/util/List;", "FIELD:Lcom/faforever/neroxis/generator/bar/News$Extras;->columns:Ljava/util/List;", "FIELD:Lcom/faforever/neroxis/generator/bar/News$Extras;->events:Ljava/util/List;").dynamicInvoker().invoke(this, o) /* invoke-custom */;
        }

        public List<Link> social() {
            return this.social;
        }

        public List<Column> columns() {
            return this.columns;
        }

        public List<Event> events() {
            return this.events;
        }
    }

    static Extras extras(Map<?, ?> map) {
        Extras fallback = defaultExtras();
        List<Link> social = links(map.get("social"));
        List<Column> columns = new ArrayList<>();
        Object obj = map.get("columns");
        if (obj instanceof List) {
            List<?> list = (List) obj;
            for (Object entry : list) {
                if (entry instanceof Map) {
                    Map<?, ?> column = (Map) entry;
                    columns.add(new Column(text(column.get("title")), links(column.get("links"))));
                }
            }
        }
        List<Event> events = new ArrayList<>();
        Object obj2 = map.get("events");
        if (obj2 instanceof List) {
            List<?> list2 = (List) obj2;
            for (Object entry2 : list2) {
                if (entry2 instanceof Map) {
                    Map<?, ?> event = (Map) entry2;
                    events.add(new Event(text(event.get("date")), text(event.get("title")), text(event.get("link"))));
                }
            }
        }
        return new Extras(social.isEmpty() ? fallback.social() : social, columns.isEmpty() ? fallback.columns() : columns, events);
    }

    private static List<Link> links(Object value) {
        List<Link> result = new ArrayList<>();
        if (value instanceof List) {
            List<?> list = (List) value;
            for (Object entry : list) {
                if (entry instanceof Map) {
                    Map<?, ?> link = (Map) entry;
                    String name = text(link.get("text")).isEmpty() ? text(link.get("name")) : text(link.get("text"));
                    result.add(new Link(name, text(link.get("url")), text(link.get("action")), colour(text(link.get("colour")))));
                }
            }
        }
        return result;
    }

    private static Color colour(String hex) {
        try {
            if (hex.isEmpty()) {
                return null;
            }
            return Color.decode(hex);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    static Extras defaultExtras() {
        List<Link> social = List.of(new Link("DISCORD", DISCORD, "", new Color(88, 101, 242)), new Link("WEBSITE", SITE, "", new Color(196, 110, 30)));
        List<Column> columns = List.of(new Column("NEW PLAYERS", List.of(new Link("Download", "https://www.beyondallreason.info/download", "", null), new Link("First steps", "https://www.beyondallreason.info/guide", "", null))), new Column("COMMUNITY", List.of(new Link("Discord", DISCORD, "", null), new Link("Maps", "https://www.beyondallreason.info/maps", "", null))), new Column("HELP", List.of(new Link("Report a bug", "", "bug", null), new Link("Trouble starting a game", "https://www.beyondallreason.info/download", "", null))));
        return new Extras(social, columns, List.of());
    }

    private static String text(Object value) {
        return value == null ? "" : String.valueOf(value);
    }

    private void show(List<Post> loaded) {
        Post featured = loaded.stream().filter((v0) -> {
            return v0.featured();
        }).findFirst().orElse(loaded.isEmpty() ? null : (Post) loaded.getFirst());
        this.hero.post = featured;
        this.hero.status = featured == null ? "No news yet." : null;
        this.hero.setCursor(Cursor.getPredefinedCursor(featured != null ? 12 : 0));
        List<Post> rest = new ArrayList<>(loaded);
        rest.remove(featured);
        this.carousel.setPosts((!rest.isEmpty() || featured == null) ? rest : List.of(featured));
        loaded.forEach(post -> {
            picture(post.image(), this.hero);
        });
        this.hero.repaint();
    }

    private void act(Link link) {
        switch (link.action()) {
            case "bug":
                this.reportBug.run();
                break;
            case "prime":
                this.openPrime.run();
                break;
            default:
                browse(link.url());
                break;
        }
    }

    private void browse(String url) {
        if (url.isEmpty()) {
            return;
        }
        try {
            Desktop.getDesktop().browse(URI.create(url.startsWith("http") ? url : base() + "/" + url));
        } catch (Exception e) {
        }
    }

    private void open(Post post) {
        if (!post.link().isEmpty()) {
            browse(post.link());
            return;
        }
        Window owner = SwingUtilities.getWindowAncestor(this.page);
        JDialog dialog = new JDialog(owner, post.title(), Dialog.ModalityType.APPLICATION_MODAL);
        Reader reader = new Reader(this, post);
        JScrollPane scroll = new JScrollPane(reader);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getViewport().setBackground(TILE);
        scroll.setHorizontalScrollBarPolicy(31);
        scroll.getVerticalScrollBar().setUnitIncrement(24);
        dialog.setContentPane(scroll);
        dialog.setSize(760, Math.min(820, reader.getPreferredSize().height + 40));
        dialog.setLocationRelativeTo(owner);
        dialog.setVisible(true);
    }

    private BufferedImage picture(String image, JComponent repaint) {
        if (image.isEmpty()) {
            return null;
        }
        BufferedImage known = this.pictures.get(image);
        if (known != null || this.asked.putIfAbsent(image, true) != null) {
            return known;
        }
        String url = image.startsWith("http") ? image : base() + "/" + image.replaceFirst("^/", "");
        this.loader.execute(() -> {
            try {
                HttpResponse<byte[]> response = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(6L)).followRedirects(HttpClient.Redirect.NORMAL).build().send(HttpRequest.newBuilder(URI.create(url)).timeout(Duration.ofSeconds(15L)).build(), HttpResponse.BodyHandlers.ofByteArray());
                BufferedImage read = response.statusCode() == 200 ? ImageIO.read(new ByteArrayInputStream((byte[]) response.body())) : null;
                if (read != null) {
                    this.pictures.put(image, read);
                    SwingUtilities.invokeLater(() -> {
                        this.hero.repaint();
                        this.carousel.repaint();
                    });
                }
            } catch (Exception e) {
            }
        });
        return null;
    }

    private void paintPicture(Graphics2D g, BufferedImage picture, int x, int y, int width, int height) {
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        Shape clip = g.getClip();
        g.clipRect(x, y, width, height);
        if (picture != null) {
            double scale = Math.max(((double) width) / ((double) picture.getWidth()), ((double) height) / ((double) picture.getHeight()));
            int w = (int) Math.ceil(((double) picture.getWidth()) * scale);
            int h = (int) Math.ceil(((double) picture.getHeight()) * scale);
            g.drawImage(picture, x + ((width - w) / 2), y + ((height - h) / 2), w, h, (ImageObserver) null);
        } else {
            g.setPaint(new GradientPaint(x, y, new Color(40, 34, 30), x, y + height, new Color(14, 15, 20)));
            g.fillRect(x, y, width, height);
            if (this.logo != null) {
                int size = (Math.min(width, height) * 11) / 20;
                g.drawImage(this.logo, x + ((width - size) / 2), y + ((height - size) / 2), size, size, (ImageObserver) null);
            }
        }
        g.setClip(clip);
    }

    static List<String> wrap(String text, FontMetrics metrics, int width, int max) {
        String last;
        List<String> lines = new ArrayList<>();
        String[] paragraphs = text.split("\n");
        loop0: for (String paragraph : paragraphs) {
            StringBuilder line = new StringBuilder();
            for (String word : paragraph.trim().split("\\s+")) {
                if (!word.isEmpty()) {
                    String tried = line.isEmpty() ? word : String.valueOf(line) + " " + word;
                    if (metrics.stringWidth(tried) <= width || line.isEmpty()) {
                        line.setLength(0);
                        line.append(tried);
                    } else {
                        lines.add(line.toString());
                        if (lines.size() == max) {
                            break loop0;
                        }
                        line.setLength(0);
                        line.append(word);
                    }
                }
            }
            lines.add(line.toString());
            if (lines.size() == max) {
                break;
            }
        }
        int total = String.join(" ", paragraphs).trim().split("\\s+").length;
        int shown = String.join(" ", lines).trim().isEmpty() ? 0 : String.join(" ", lines).trim().split("\\s+").length;
        if (shown < total && !lines.isEmpty()) {
            String strSubstring = (String) lines.getLast();
            while (true) {
                last = strSubstring;
                if (last.isEmpty() || metrics.stringWidth(last + "...") <= width) {
                    break;
                }
                strSubstring = last.substring(0, last.length() - 1);
            }
            lines.set(lines.size() - 1, last.stripTrailing() + "...");
        }
        return lines;
    }

    private static Graphics2D smooth(Graphics graphics) {
        Graphics2D g = graphics.create();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);
        g.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
        return g;
    }

    private static Font font(int style, float size) {
        Font base = UIManager.getFont("Label.font");
        return (base != null ? base : new Font("SansSerif", 0, 13)).deriveFont(style, size);
    }

    private static int drawLines(Graphics2D g, List<String> lines, int x, int y, int lineHeight) {
        FontMetrics metrics = g.getFontMetrics();
        for (String line : lines) {
            g.drawString(line, x, y + metrics.getAscent());
            y += lineHeight;
        }
        return y;
    }

    /* JADX INFO: loaded from: app_classes.zip:com/faforever/neroxis/generator/bar/News$Clickable.class */
    private static abstract class Clickable extends JComponent {
        boolean hover;

        abstract void clicked(MouseEvent e);

        Clickable() {
            setOpaque(false);
            setCursor(Cursor.getPredefinedCursor(12));
            addMouseListener(new MouseAdapter(this) { // from class: com.faforever.neroxis.generator.bar.News.Clickable.1
                final /* synthetic */ Clickable this$0;

                {
                    Objects.requireNonNull(this);
                    this.this$0 = this;
                }

                public void mouseEntered(MouseEvent e) {
                    this.this$0.hover = true;
                    this.this$0.repaint();
                }

                public void mouseExited(MouseEvent e) {
                    this.this$0.hover = false;
                    this.this$0.repaint();
                }

                public void mouseReleased(MouseEvent e) {
                    if (SwingUtilities.isLeftMouseButton(e) && this.this$0.contains(e.getPoint())) {
                        this.this$0.clicked(e);
                    }
                }
            });
        }
    }

    /* JADX INFO: loaded from: app_classes.zip:com/faforever/neroxis/generator/bar/News$Hero.class */
    private final class Hero extends Clickable {
        Post post;
        String status;
        final /* synthetic */ News this$0;

        private Hero(final News this$0) {
            Objects.requireNonNull(this$0);
            this.this$0 = this$0;
            this.status = "Loading the news...";
        }

        @Override // com.faforever.neroxis.generator.bar.News.Clickable
        void clicked(MouseEvent e) {
            if (this.post != null) {
                this.this$0.open(this.post);
            }
        }

        protected void paintComponent(Graphics graphics) {
            int y;
            Graphics2D g = News.smooth(graphics);
            int width = getWidth();
            int height = getHeight();
            g.setColor((!this.hover || this.post == null) ? News.TILE : News.TILE_HOVER);
            g.fillRect(0, 0, width, height);
            int pictureWidth = Math.min((width * 45) / 100, (height * 16) / 9);
            this.this$0.paintPicture(g, this.post != null ? this.this$0.picture(this.post.image(), this) : null, 0, 0, pictureWidth, height);
            int x = pictureWidth + 22;
            int textWidth = (width - x) - 22;
            if (this.post == null) {
                g.setFont(News.font(0, 14.0f));
                g.setColor(BarLauncher.MUTED);
                News.drawLines(g, News.wrap(this.status != null ? this.status : "", g.getFontMetrics(), textWidth, 4), x, 22, 20);
                g.dispose();
                return;
            }
            g.setFont(News.font(1, 18.0f));
            g.setColor(Color.WHITE);
            int y2 = News.drawLines(g, News.wrap(this.post.title().toUpperCase(Locale.ROOT), g.getFontMetrics(), textWidth, 2), x, 22, 25);
            if (!this.post.date().isEmpty()) {
                g.setFont(News.font(0, 12.0f));
                g.setColor(BarLauncher.ACCENT);
                News.drawLines(g, List.of(this.post.date()), x, y2 + 2, 16);
                y = y2 + 26;
            } else {
                y = y2 + 8;
            }
            g.setFont(News.font(0, 14.0f));
            g.setColor(News.LIGHT_TEXT);
            int room = Math.max(1, ((height - y) - 16) / 21);
            News.drawLines(g, News.wrap(this.post.text(), g.getFontMetrics(), textWidth, room), x, y, 21);
            g.dispose();
        }
    }

    /* JADX INFO: loaded from: app_classes.zip:com/faforever/neroxis/generator/bar/News$Tile.class */
    private final class Tile extends Clickable {
        private final Color fill;
        private final String first;
        private final String second;
        private final float size;
        private final Runnable action;
        final /* synthetic */ News this$0;

        Tile(final News this$0, Color fill, String first, String second, float size, Runnable action) {
            Objects.requireNonNull(this$0);
            this.this$0 = this$0;
            this.fill = fill;
            this.first = first;
            this.second = second;
            this.size = size;
            this.action = action;
        }

        @Override // com.faforever.neroxis.generator.bar.News.Clickable
        void clicked(MouseEvent e) {
            this.action.run();
        }

        protected void paintComponent(Graphics graphics) {
            Graphics2D g = News.smooth(graphics);
            g.setColor(this.hover ? this.fill.brighter() : this.fill);
            if (this.hover && this.fill.equals(News.DEEP)) {
                g.setColor(News.TILE_HOVER);
            }
            g.fillRect(0, 0, getWidth(), getHeight());
            g.setColor(Color.WHITE);
            if (this.second.isEmpty()) {
                g.setFont(News.font(1, this.size));
                FontMetrics metrics = g.getFontMetrics();
                g.drawString(this.first, (getWidth() - metrics.stringWidth(this.first)) / 2, ((getHeight() - metrics.getHeight()) / 2) + metrics.getAscent());
            } else {
                g.setFont(News.font(1, this.size));
                FontMetrics top = g.getFontMetrics();
                g.setFont(News.font(1, this.size + 4.0f));
                FontMetrics bottom = g.getFontMetrics();
                int total = top.getHeight() + bottom.getHeight();
                int y = (getHeight() - total) / 2;
                g.setFont(News.font(1, this.size));
                g.drawString(this.first, (getWidth() - top.stringWidth(this.first)) / 2, y + top.getAscent());
                g.setFont(News.font(1, this.size + 4.0f));
                g.setColor(BarLauncher.ACCENT);
                String spaced = String.join("\u2009", this.second.split(""));
                g.drawString(spaced, (getWidth() - bottom.stringWidth(spaced)) / 2, y + top.getHeight() + bottom.getAscent());
            }
            g.dispose();
        }
    }

    private void showSide(List<Link> social) {
        this.side.removeAll();
        JPanel row = new JPanel(new GridLayout(1, Math.max(1, social.size()), 0, 0));
        row.setOpaque(false);
        row.setPreferredSize(new Dimension(340, 62));
        for (Link link : social) {
            row.add(new Tile(this, link.colour() != null ? link.colour() : HEAD, link.text().toUpperCase(Locale.ROOT), "", 14.0f, () -> {
                act(link);
            }));
        }
        this.side.add(row, "North");
        this.side.add(new Tile(this, DEEP, "MAP STUDIO", "STUDIO", 26.0f, this.openPrime), "Center");
        Tile bug = new Tile(this, HEAD, "Report a Bug", "", 18.0f, this.reportBug);
        bug.setPreferredSize(new Dimension(340, 50));
        this.side.add(bug, "South");
        this.side.revalidate();
        this.side.repaint();
    }

    /* JADX INFO: loaded from: app_classes.zip:com/faforever/neroxis/generator/bar/News$Card.class */
    private final class Card extends Clickable {
        private final Post post;
        final /* synthetic */ News this$0;

        Card(final News this$0, Post post) {
            Objects.requireNonNull(this$0);
            this.this$0 = this$0;
            this.post = post;
        }

        @Override // com.faforever.neroxis.generator.bar.News.Clickable
        void clicked(MouseEvent e) {
            this.this$0.open(this.post);
        }

        protected void paintComponent(Graphics graphics) {
            Graphics2D g = News.smooth(graphics);
            int width = getWidth();
            int height = getHeight();
            g.setColor(this.hover ? News.TILE_HOVER : News.TILE);
            g.fillRect(0, 0, width, height);
            int pictureHeight = Math.min(height / 2, (width * 5) / 8);
            this.this$0.paintPicture(g, this.this$0.picture(this.post.image(), this), 0, 0, width, pictureHeight);
            if (this.hover) {
                g.setColor(BarLauncher.ACCENT);
                g.fillRect(0, pictureHeight - 3, width, 3);
            }
            int textWidth = width - 28;
            g.setFont(News.font(1, 15.0f));
            g.setColor(Color.WHITE);
            int y = News.drawLines(g, News.wrap(this.post.title(), g.getFontMetrics(), textWidth, 2), 14, pictureHeight + 14, 20) + 6;
            g.setFont(News.font(0, 13.0f));
            g.setColor(News.LIGHT_TEXT);
            int room = Math.max(1, ((height - y) - 10) / 19);
            News.drawLines(g, News.wrap(this.post.text(), g.getFontMetrics(), textWidth, room), 14, y, 19);
            g.dispose();
        }
    }

    /* JADX INFO: loaded from: app_classes.zip:com/faforever/neroxis/generator/bar/News$Arrow.class */
    private final class Arrow extends Clickable {
        private final int step;
        final /* synthetic */ News this$0;

        Arrow(final News this$0, int step) {
            Objects.requireNonNull(this$0);
            this.this$0 = this$0;
            this.step = step;
            setPreferredSize(new Dimension(34, 100));
        }

        @Override // com.faforever.neroxis.generator.bar.News.Clickable
        void clicked(MouseEvent e) {
            this.this$0.carousel.move(this.step);
        }

        protected void paintComponent(Graphics graphics) {
            Graphics2D g = News.smooth(graphics);
            boolean possible = this.this$0.carousel.canMove(this.step);
            g.setColor((this.hover && possible) ? new Color(62, 67, 80) : News.HEAD);
            g.fillRect(0, 0, getWidth(), getHeight());
            if (possible) {
                g.setColor(this.hover ? Color.WHITE : News.LIGHT_TEXT);
                g.setStroke(new BasicStroke(3.0f, 1, 1));
                int cx = getWidth() / 2;
                int cy = getHeight() / 2;
                int d = this.step < 0 ? -1 : 1;
                g.drawPolyline(new int[]{cx - (5 * d), cx + (5 * d), cx - (5 * d)}, new int[]{cy - 11, cy, cy + 11}, 3);
            }
            g.dispose();
        }
    }

    /* JADX INFO: loaded from: app_classes.zip:com/faforever/neroxis/generator/bar/News$Carousel.class */
    private final class Carousel extends JPanel {
        private final Arrow left;
        private final Arrow right;
        private final JPanel strip;
        private final List<Card> cards;
        private int first;
        final /* synthetic */ News this$0;

        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        Carousel(final News this$0) {
            super(new BorderLayout(18, 0));
            Objects.requireNonNull(this$0);
            this.this$0 = this$0;
            this.left = new Arrow(this.this$0, -1);
            this.right = new Arrow(this.this$0, 1);
            this.strip = new JPanel(this, null) { // from class: com.faforever.neroxis.generator.bar.News.Carousel.1
                final /* synthetic */ Carousel this$1;

                {
                    Objects.requireNonNull(this);
                    this.this$1 = this;
                }

                public void doLayout() {
                    this.this$1.layoutCards();
                }
            };
            this.cards = new ArrayList();
            setOpaque(false);
            this.strip.setOpaque(false);
            add(this.left, "West");
            add(this.strip, "Center");
            add(this.right, "East");
            this.strip.addMouseWheelListener(e -> {
                move(e.getWheelRotation() > 0 ? 1 : -1);
            });
        }

        void setPosts(List<Post> posts) {
            this.cards.clear();
            this.strip.removeAll();
            for (Post post : posts) {
                Card card = new Card(this.this$0, post);
                this.cards.add(card);
                this.strip.add(card);
            }
            this.first = 0;
            this.strip.revalidate();
            repaint();
        }

        private int visible() {
            int width = this.strip.getWidth();
            return Math.max(1, (width + 18) / 238);
        }

        boolean canMove(int step) {
            if (step < 0) {
                return this.first > 0;
            }
            return this.first + visible() < this.cards.size();
        }

        void move(int step) {
            if (canMove(step)) {
                this.first += step;
                layoutCards();
                repaint();
            }
        }

        private void layoutCards() {
            int count = visible();
            this.first = Math.max(0, Math.min(this.first, this.cards.size() - count));
            int width = this.strip.getWidth();
            int cardWidth = (width - ((count - 1) * 18)) / count;
            for (int i = 0; i < this.cards.size(); i++) {
                int slot = i - this.first;
                Card card = this.cards.get(i);
                card.setVisible(slot >= 0 && slot < count);
                card.setBounds(new Rectangle(slot * (cardWidth + 18), 0, cardWidth, this.strip.getHeight()));
            }
            this.left.repaint();
            this.right.repaint();
        }
    }

    private JPanel column(final String title, List<JComponent> lines) {
        JPanel column = new JPanel(new BorderLayout(0, 0));
        column.setOpaque(false);
        JComponent head = new JComponent(this) { // from class: com.faforever.neroxis.generator.bar.News.1
            final /* synthetic */ News this$0;

            {
                Objects.requireNonNull(this);
                this.this$0 = this;
            }

            protected void paintComponent(Graphics graphics) {
                Graphics2D g = News.smooth(graphics);
                g.setColor(News.HEAD);
                g.fillRect(0, 0, getWidth(), getHeight());
                g.setFont(News.font(1, 16.0f));
                g.setColor(Color.WHITE);
                FontMetrics metrics = g.getFontMetrics();
                g.drawString(title, (getWidth() - metrics.stringWidth(title)) / 2, ((getHeight() - metrics.getHeight()) / 2) + metrics.getAscent());
                g.dispose();
            }
        };
        head.setPreferredSize(new Dimension(200, 46));
        column.add(head, "North");
        JPanel body = new JPanel(this, new GridLayout(0, 1, 0, 0)) { // from class: com.faforever.neroxis.generator.bar.News.2
            final /* synthetic */ News this$0;

            {
                Objects.requireNonNull(this);
                this.this$0 = this;
            }

            protected void paintComponent(Graphics graphics) {
                graphics.setColor(News.TILE);
                graphics.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        body.setOpaque(false);
        body.setBorder(BorderFactory.createEmptyBorder(6, 0, 8, 0));
        Objects.requireNonNull(body);
        lines.forEach((v1) -> {
            r1.add(v1);
        });
        JPanel holder = new JPanel(new BorderLayout());
        holder.setOpaque(false);
        holder.add(body, "North");
        column.add(holder, "Center");
        return column;
    }

    /* JADX INFO: loaded from: app_classes.zip:com/faforever/neroxis/generator/bar/News$Line.class */
    private final class Line extends Clickable {
        private final String left;
        private final String right;
        private final Runnable action;
        final /* synthetic */ News this$0;

        Line(final News this$0, String left, String right, Runnable action) {
            Objects.requireNonNull(this$0);
            this.this$0 = this$0;
            this.left = left;
            this.right = right;
            this.action = action;
            if (action == null) {
                setCursor(Cursor.getDefaultCursor());
            }
            setPreferredSize(new Dimension(200, 34));
        }

        @Override // com.faforever.neroxis.generator.bar.News.Clickable
        void clicked(MouseEvent e) {
            if (this.action != null) {
                this.action.run();
            }
        }

        protected void paintComponent(Graphics graphics) {
            Graphics2D g = News.smooth(graphics);
            g.setFont(News.font(0, 14.0f));
            FontMetrics metrics = g.getFontMetrics();
            int y = ((getHeight() - metrics.getHeight()) / 2) + metrics.getAscent();
            g.setColor((!this.hover || this.action == null) ? News.LIGHT_TEXT : Color.WHITE);
            if (this.right.isEmpty()) {
                g.drawString(this.left, (getWidth() - metrics.stringWidth(this.left)) / 2, y);
            } else {
                g.setColor(BarLauncher.ACCENT);
                g.drawString(this.left, 18, y);
                g.setColor((!this.hover || this.action == null) ? News.LIGHT_TEXT : Color.WHITE);
                String shown = (String) News.wrap(this.right, metrics, getWidth() - 150, 1).getFirst();
                g.drawString(shown, 130, y);
            }
            g.dispose();
        }
    }

    private void showExtras(Extras extras) {
        showSide(extras.social());
        this.bottom.removeAll();
        GridBagConstraints at = new GridBagConstraints();
        at.fill = 1;
        at.gridy = 0;
        at.weighty = 1.0d;
        int x = 0;
        for (Column column : extras.columns()) {
            List<JComponent> lines = new ArrayList<>();
            for (Link link : column.links()) {
                lines.add(new Line(this, link.text(), "", () -> {
                    act(link);
                }));
            }
            int i = x;
            x++;
            at.gridx = i;
            at.weightx = 0.0d;
            at.insets = new Insets(0, 0, 0, 18);
            JPanel panel = column(column.title(), lines);
            panel.setPreferredSize(new Dimension(220, 170));
            panel.setMinimumSize(new Dimension(140, 170));
            this.bottom.add(panel, at);
        }
        List<JComponent> events = new ArrayList<>();
        for (Event event : extras.events()) {
            events.add(new Line(this, event.date(), event.title(), event.link().isEmpty() ? null : () -> {
                browse(event.link());
            }));
        }
        if (events.isEmpty()) {
            events.add(new Line(this, "Nothing on the calendar yet", "", null));
            events.add(new Line(this, "Want to run a tournament? Ask on Discord", "", () -> {
                browse(DISCORD);
            }));
        }
        at.gridx = x;
        at.weightx = 1.0d;
        at.insets = new Insets(0, 0, 0, 0);
        JPanel panel2 = column("UPCOMING TOURNAMENTS / EVENTS", events);
        panel2.setPreferredSize(new Dimension(300, 170));
        panel2.setMinimumSize(new Dimension(200, 170));
        this.bottom.add(panel2, at);
        this.bottom.revalidate();
        this.bottom.repaint();
    }

    /* JADX INFO: loaded from: app_classes.zip:com/faforever/neroxis/generator/bar/News$Reader.class */
    private final class Reader extends JComponent implements Scrollable {
        private static final int WIDTH = 740;
        private final Post post;
        final /* synthetic */ News this$0;

        Reader(final News this$0, Post post) {
            Objects.requireNonNull(this$0);
            this.this$0 = this$0;
            this.post = post;
            setOpaque(true);
        }

        private int pictureHeight() {
            return this.post.image().isEmpty() ? 0 : 416;
        }

        public Dimension getPreferredSize() {
            BufferedImage scratch = new BufferedImage(1, 1, 2);
            Graphics2D g = scratch.createGraphics();
            g.setFont(News.font(1, 20.0f));
            int title = News.wrap(this.post.title(), g.getFontMetrics(), 692, 99).size() * 28;
            g.setFont(News.font(0, 14.5f));
            int text = News.wrap(this.post.text(), g.getFontMetrics(), 692, 999).size() * 22;
            g.dispose();
            return new Dimension(WIDTH, pictureHeight() + 24 + title + 30 + text + 30);
        }

        protected void paintComponent(Graphics graphics) {
            Graphics2D g = News.smooth(graphics);
            g.setColor(News.TILE);
            g.fillRect(0, 0, getWidth(), getHeight());
            int y = 0;
            if (pictureHeight() > 0) {
                this.this$0.paintPicture(g, this.this$0.picture(this.post.image(), this), 0, 0, getWidth(), pictureHeight());
                y = pictureHeight();
            }
            g.setFont(News.font(1, 20.0f));
            g.setColor(Color.WHITE);
            int y2 = News.drawLines(g, News.wrap(this.post.title(), g.getFontMetrics(), getWidth() - 48, 99), 24, y + 24, 28);
            g.setFont(News.font(0, 12.5f));
            g.setColor(BarLauncher.ACCENT);
            News.drawLines(g, List.of(this.post.date()), 24, y2 + 4, 18);
            g.setFont(News.font(0, 14.5f));
            g.setColor(News.LIGHT_TEXT);
            News.drawLines(g, News.wrap(this.post.text(), g.getFontMetrics(), getWidth() - 48, 999), 24, y2 + 30, 22);
            g.dispose();
        }

        public Dimension getPreferredScrollableViewportSize() {
            return getPreferredSize();
        }

        public int getScrollableUnitIncrement(Rectangle visible, int orientation, int direction) {
            return 22;
        }

        public int getScrollableBlockIncrement(Rectangle visible, int orientation, int direction) {
            return visible.height - 44;
        }

        public boolean getScrollableTracksViewportWidth() {
            return true;
        }

        public boolean getScrollableTracksViewportHeight() {
            return false;
        }
    }
}
