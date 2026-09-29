package com.faforever.neroxis.generator.bar;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.runtime.ObjectMethods;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.UUID;
import javax.net.ssl.SSLSocketFactory;

/* JADX INFO: loaded from: app_classes.zip:com/faforever/neroxis/generator/bar/LobbyClient.class */
final class LobbyClient {
    static final int STATUS_IN_GAME = 1;
    static final int STATUS_AWAY = 2;
    static final int STATUS_BOT = 64;
    static final int BATTLE_READY = 2;
    static final int BATTLE_PLAYER = 1024;
    private static final int BATTLE_SYNCED = 4194304;
    private static final int BATTLE_UNSYNCED = 8388608;
    private final Listener listener;
    private String partyId;
    private volatile boolean loginComplete;
    private Socket socket;
    private OutputStream output;
    private String userName;
    private boolean loggedIn;
    private long acceptedAt;
    private long pendingSince;
    private long queueAsked;
    private boolean queueAskPending;
    private int wantSide;
    private boolean ready;
    private boolean synced;
    private long lastSent;
    private long lastStatusTime;
    private static Path logFile;
    static final List<AiKind> AI_KINDS = List.of(new AiKind("BARbarIAn – Hard, aggressive", "BARb", "BARb_HardAggressive"), new AiKind("BARbarIAn – Hard, balanced", "BARb", "BARb_Hard"), new AiKind("BARbarIAn – Medium", "BARb", "BARb_Medium"), new AiKind("BARbarIAn – Easy", "BARb", "BARb_Easy"), new AiKind("SimpleAI (easy)", "SimpleAI", "SimpleAI"), new AiKind("SimpleDefenderAI (easy)", "SimpleDefenderAI", "SimpleDefenderAI"), new AiKind("SimpleConstructorAI (easy)", "SimpleConstructorAI", "SimpleConstructorAI"), new AiKind("ScavengersAI (PvE)", "ScavengersAI", "ScavengersAI"), new AiKind("RaptorsAI (PvE)", "RaptorsAI", "RaptorsAI"));
    private final Map<String, User> users = new TreeMap(String.CASE_INSENSITIVE_ORDER);
    private final Map<Integer, Battle> battles = new LinkedHashMap();
    private final Set<String> friends = new TreeSet(String.CASE_INSENSITIVE_ORDER);
    private final Set<String> friendRequests = new TreeSet(String.CASE_INSENSITIVE_ORDER);
    private final Map<String, Set<String>> channels = new TreeMap();
    private final Map<String, Participant> participants = new TreeMap(String.CASE_INSENSITIVE_ORDER);
    private final Map<Integer, float[]> startBoxes = new TreeMap();
    private final Map<String, String> modOptions = new TreeMap();
    private final Set<String> partyMembers = new TreeSet(String.CASE_INSENSITIVE_ORDER);
    private final Set<String> partyInvited = new TreeSet(String.CASE_INSENSITIVE_ORDER);
    private final Map<String, Set<String>> partyInvitations = new LinkedHashMap();
    private final List<String> partyInvitesToSend = new ArrayList();
    private final StringBuilder agreement = new StringBuilder();
    private long lastSessionMillis = -1;
    private int joinedBattle = -1;
    private int pendingBattle = -1;
    private int sentStatus = -1;
    private String scriptPassword = "";
    private boolean wantPlayer = true;
    private final List<String> joinQueue = new ArrayList();
    private int wantAlly = -1;
    private final Map<String, String> skills = new TreeMap(String.CASE_INSENSITIVE_ORDER);
    private int lastSentStatus = -1;
    private int wantColour = 16744192;
    private int lastSentColour = -1;

    /* JADX INFO: loaded from: app_classes.zip:com/faforever/neroxis/generator/bar/LobbyClient$User.class */
    static final class User {
        final String name;
        final String country;
        int status;

        User(String name, String country) {
            this.name = name;
            this.country = country;
        }

        boolean inGame() {
            return (this.status & 1) != 0;
        }

        boolean bot() {
            return (this.status & 64) != 0;
        }

        boolean away() {
            return (this.status & 2) != 0;
        }
    }

    /* JADX INFO: loaded from: app_classes.zip:com/faforever/neroxis/generator/bar/LobbyClient$Battle.class */
    static final class Battle {
        final int id;
        final String founder;
        final String ip;
        final int port;
        final boolean passworded;
        final String engine;
        String title;
        final String game;
        String map;
        int spectators;
        boolean locked;
        String host;
        final Set<String> users = new TreeSet(String.CASE_INSENSITIVE_ORDER);

        Battle(int id, String founder, String ip, int port, boolean passworded, String engine, String map, String title, String game) {
            this.id = id;
            this.founder = founder;
            this.ip = ip;
            this.port = port;
            this.passworded = passworded;
            this.engine = engine;
            this.map = map;
            this.title = title;
            this.game = game;
        }

        int userCount() {
            return Math.max(0, this.users.size() - 1);
        }
    }

    /* JADX INFO: loaded from: app_classes.zip:com/faforever/neroxis/generator/bar/LobbyClient$Participant.class */
    static final class Participant {
        final String name;
        final String owner;
        int battleStatus;
        int colour = 16744192;

        Participant(String name, String owner) {
            this.name = name;
            this.owner = owner;
        }

        boolean player() {
            return (this.battleStatus & 1024) != 0;
        }

        boolean ready() {
            return (this.battleStatus & 2) != 0;
        }

        int allyTeam() {
            return (this.battleStatus >> 6) & 15;
        }

        int team() {
            return (this.battleStatus >> 2) & 15;
        }

        int side() {
            return (this.battleStatus >> 24) & 15;
        }
    }

    /* JADX INFO: loaded from: app_classes.zip:com/faforever/neroxis/generator/bar/LobbyClient$Listener.class */
    interface Listener {
        void onState(String message);

        void onAgreement(String text);

        void onUsersChanged();

        void onBattlesChanged();

        void onChat(String channel, String user, String message, boolean action);

        void onPrivateChat(String user, String message, boolean outgoing);

        void onFriendsChanged();

        void onBattleRoomChanged();

        void onBattleChat(String user, String message, boolean action);

        void onGameStart(Battle battle, String scriptPassword);

        default void onGameEnd(Battle battle) {
        }

        default void onPartyChanged() {
        }

        default void onRing(String user) {
        }
    }

    synchronized List<String> getJoinQueue() {
        return new ArrayList(this.joinQueue);
    }

    synchronized int queuePosition() {
        for (int i = 0; i < this.joinQueue.size(); i++) {
            if (this.joinQueue.get(i).equalsIgnoreCase(this.userName)) {
                return i + 1;
            }
        }
        return 0;
    }

    void leaveQueue() {
        sayBattle("$leaveq");
    }

    private void askQueue() {
        synchronized (this) {
            if (this.joinedBattle < 0 || this.queueAskPending) {
                return;
            }
            this.queueAskPending = true;
            long wait = Math.max(300L, 1500 - (System.currentTimeMillis() - this.queueAsked));
            Thread asker = new Thread(() -> {
                boolean inRoom;
                try {
                    Thread.sleep(wait);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                synchronized (this) {
                    this.queueAskPending = false;
                    this.queueAsked = System.currentTimeMillis();
                    inRoom = this.joinedBattle >= 0;
                }
                if (inRoom) {
                    send("c.battle.queue_status");
                }
            }, "queue-status");
            asker.setDaemon(true);
            asker.start();
        }
    }

    LobbyClient(Listener listener) {
        this.listener = listener;
    }

    void connect(String host, int port, boolean tls, String user, char[] password, String registerEmail) {
        connectWithHash(host, port, tls, user, hashPassword(password), registerEmail);
    }

    synchronized void loginOffline(String user) {
        disconnect();
        this.loggedIn = true;
        this.loginComplete = true;
        this.acceptedAt = System.currentTimeMillis();
        this.userName = user;
        if (this.users.get(user) == null) {
            this.users.put(user, new User(user, "IT"));
        }
        if (this.listener != null) {
            this.listener.onState(null);
            this.listener.onBattlesChanged();
            this.listener.onUsersChanged();
        }
    }

    static String hashPassword(char[] password) {
        try {
            byte[] bytes = new String(password).getBytes(StandardCharsets.UTF_8);
            return Base64.getEncoder().encodeToString(MessageDigest.getInstance("MD5").digest(bytes));
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(e);
        }
    }

    static void logTo(Path file) {
        logFile = file;
    }

    private static synchronized void log(String text) {
        Path file = logFile;
        if (file == null) {
            return;
        }
        try {
            if (Files.exists(file, new LinkOption[0]) && Files.size(file) > 1000000) {
                Files.delete(file);
            }
            Files.writeString(file, String.valueOf(LocalDateTime.now().withNano(0)) + "  " + text + System.lineSeparator(), StandardCharsets.UTF_8, new OpenOption[]{StandardOpenOption.CREATE, StandardOpenOption.APPEND});
        } catch (IOException e) {
        }
    }

    void connectWithHash(String host, int port, boolean tls, String user, String hash, String registerEmail) {
        log("connect " + host + ":" + port + (tls ? " tls" : "") + " as " + user + (registerEmail != null ? " (register)" : ""));
        disconnect();
        Thread thread = new Thread(() -> {
            run(host, port, tls, user, hash, registerEmail);
        }, "lobby-connection");
        thread.setDaemon(true);
        thread.start();
    }

    private void run(String host, int port, boolean tls, String user, String passwordHash, String registerEmail) {
        Socket connection = null;
        try {
            if (tls) {
                connection = SSLSocketFactory.getDefault().createSocket();
            } else {
                connection = new Socket();
            }
            connection.connect(new InetSocketAddress(host, port), 10000);
            connection.setKeepAlive(true);
            synchronized (this) {
                this.socket = connection;
                this.output = connection.getOutputStream();
                this.userName = user;
            }
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8));
            String greeting = reader.readLine();
            if (greeting == null || !greeting.startsWith("TASSERVER")) {
                throw new IOException("this is not a lobby server");
            }
            String login = "LOGIN " + user + " " + passwordHash + " 0 * " + System.getProperty("neroxis.launcher.lobbyname", "Neroxis Launcher") + "\t" + machineId() + "\tsp b";
            if (registerEmail != null) {
                send("REGISTER " + user + " " + passwordHash + " " + registerEmail);
            } else {
                send(login);
            }
            Thread pinger = new Thread(() -> {
                ping();
            }, "lobby-ping");
            pinger.setDaemon(true);
            pinger.start();
            while (true) {
                String line = reader.readLine();
                if (line != null) {
                    if (line.equals("REGISTRATIONACCEPTED")) {
                        send(login);
                    } else {
                        handle(line);
                    }
                } else {
                    closed(connection, "The server closed the connection");
                    return;
                }
            }
        } catch (IOException e) {
            closed(connection, "Connection failed: " + e.getMessage());
        }
    }

    private void ping() {
        Socket mine;
        synchronized (this) {
            mine = this.socket;
        }
        while (true) {
            try {
                Thread.sleep(5000L);
                synchronized (this) {
                    if (this.socket != mine || mine == null) {
                        break;
                    } else if (System.currentTimeMillis() - this.lastSent > 20000) {
                        send("PING");
                    }
                }
            } catch (InterruptedException e) {
                return;
            }
        }
    }

    private void closed(Socket connection, String message) {
        boolean current;
        synchronized (this) {
            current = this.socket == connection;
            if (current) {
                this.lastSessionMillis = (!this.loggedIn || this.acceptedAt <= 0) ? -1L : System.currentTimeMillis() - this.acceptedAt;
                this.acceptedAt = 0L;
                reset();
            }
        }
        log((current ? "closed: " : "closed (old connection): ") + message);
        if (connection != null) {
            try {
                connection.close();
            } catch (IOException e) {
            }
        }
        if (current) {
            this.listener.onState(message);
            this.listener.onUsersChanged();
            this.listener.onBattlesChanged();
            this.listener.onFriendsChanged();
            this.listener.onBattleRoomChanged();
            this.listener.onPartyChanged();
        }
    }

    private void reset() {
        this.socket = null;
        this.output = null;
        this.sentStatus = -1;
        this.loginComplete = false;
        this.partyId = null;
        this.partyMembers.clear();
        this.partyInvited.clear();
        this.partyInvitations.clear();
        this.partyInvitesToSend.clear();
        this.loggedIn = false;
        this.joinedBattle = -1;
        this.pendingBattle = -1;
        this.users.clear();
        this.battles.clear();
        this.friends.clear();
        this.friendRequests.clear();
        this.channels.clear();
        this.participants.clear();
    }

    void leaveAndDisconnect() {
        boolean inBattle;
        synchronized (this) {
            inBattle = this.joinedBattle >= 0 && this.socket != null;
        }
        if (inBattle) {
            send("LEAVEBATTLE");
        }
        disconnect();
    }

    synchronized void disconnect() {
        Socket connection = this.socket;
        if (connection != null) {
            send("EXIT");
            reset();
            try {
                connection.close();
            } catch (IOException e) {
            }
        }
    }

    private synchronized void send(String line) {
        if (this.output == null) {
            return;
        }
        try {
            this.output.write((line + "\n").getBytes(StandardCharsets.UTF_8));
            this.output.flush();
            this.lastSent = System.currentTimeMillis();
        } catch (IOException e) {
            log("send failed: " + e.getMessage());
        }
    }

    private static String machineId() {
        return String.valueOf(Math.abs(UUID.nameUUIDFromBytes(System.getProperty("user.name", "neroxis").getBytes(StandardCharsets.UTF_8)).getMostSignificantBits() % 2000000000) + 1);
    }

    private void handle(String line) {
        String str;
        if (line.startsWith("#") && line.indexOf(32) > 0) {
            line = line.substring(line.indexOf(32) + 1);
        }
        int space = line.indexOf(32);
        String command = space < 0 ? line : line.substring(0, space);
        String rest = space < 0 ? "" : line.substring(space + 1);
        String[] words = rest.split(" ");
        switch (command) {
            case "ACCEPTED":
                log("accepted as " + rest.trim());
                synchronized (this) {
                    this.loggedIn = true;
                    this.acceptedAt = System.currentTimeMillis();
                    this.userName = rest.trim();
                    break;
                }
                return;
            case "LOGININFOEND":
                this.loginComplete = true;
                send("c.telemetry.update_client_property hardware:cpuinfo " + Base64.getUrlEncoder().encodeToString("Eclipse launcher".getBytes(StandardCharsets.UTF_8)) + " eclipse");
                send("JOIN main");
                refreshFriends();
                this.listener.onState(null);
                this.listener.onBattlesChanged();
                this.listener.onUsersChanged();
                return;
            case "DENIED":
                switch (rest.trim()) {
                    case "Incorrect code":
                        str = "That code was not right. Sign in again and type the 6 digits from the email.";
                        break;
                    case "Invalid password":
                        str = "Wrong password. Forgot it? Press \"Forgot password?\" below.";
                        break;
                    default:
                        if (rest.trim().startsWith("Flood protection")) {
                            str = "Signed in too often, again in 20 seconds.";
                            break;
                        } else {
                            str = "Login denied: " + rest;
                            break;
                        }
                        break;
                }
                fail(str);
                return;
            case "REGISTRATIONDENIED":
                fail("Registration denied: " + rest);
                return;
            case "AGREEMENT":
                this.agreement.append(rest).append('\n');
                return;
            case "AGREEMENTEND":
                String text = this.agreement.toString();
                this.agreement.setLength(0);
                this.listener.onAgreement(text);
                return;
            case "ADDUSER":
                synchronized (this) {
                    this.users.put(words[0], new User(words[0], words.length > 1 ? words[1] : "??"));
                    break;
                }
                changedUsers();
                return;
            case "REMOVEUSER":
                synchronized (this) {
                    this.users.remove(words[0]);
                    break;
                }
                changedUsers();
                return;
            case "CLIENTSTATUS":
                clientStatus(words);
                return;
            case "BATTLEOPENED":
                battleOpened(rest, words);
                return;
            case "BATTLECLOSED":
                synchronized (this) {
                    this.battles.remove(Integer.valueOf(parse(words[0])));
                    if (this.joinedBattle == parse(words[0])) {
                        this.joinedBattle = -1;
                        this.participants.clear();
                    }
                    break;
                }
                changedBattles();
                this.listener.onBattleRoomChanged();
                return;
            case "UPDATEBATTLEINFO":
                synchronized (this) {
                    Battle battle = this.battles.get(Integer.valueOf(parse(words[0])));
                    if (battle != null && words.length >= 5) {
                        battle.spectators = parse(words[1]);
                        battle.locked = "1".equals(words[2]);
                        battle.map = rest.split(" ", 5)[4];
                    }
                    break;
                }
                changedBattles();
                if (parse(words[0]) == this.joinedBattle) {
                    this.listener.onBattleRoomChanged();
                    return;
                }
                return;
            case "JOINEDBATTLE":
                synchronized (this) {
                    Battle battle2 = this.battles.get(Integer.valueOf(parse(words[0])));
                    if (battle2 != null) {
                        battle2.users.add(words[1]);
                        if ((battle2.host == null || !this.loginComplete) && !words[1].equalsIgnoreCase(battle2.founder)) {
                            battle2.host = words[1];
                        }
                    }
                    if (parse(words[0]) == this.joinedBattle && !this.participants.containsKey(words[1])) {
                        this.participants.put(words[1], new Participant(words[1], null));
                    }
                    break;
                }
                changedBattles();
                roomChanged(parse(words[0]));
                return;
            case "LEFTBATTLE":
                synchronized (this) {
                    Battle battle3 = this.battles.get(Integer.valueOf(parse(words[0])));
                    if (battle3 != null) {
                        battle3.users.remove(words[1]);
                        if (words[1].equalsIgnoreCase(battle3.host)) {
                            battle3.host = battle3.users.stream().filter(name -> {
                                return !name.equalsIgnoreCase(battle3.founder);
                            }).findFirst().orElse(null);
                        }
                    }
                    boolean self = words[1].equalsIgnoreCase(this.userName) && parse(words[0]) == this.joinedBattle;
                    if (self) {
                        this.joinedBattle = -1;
                        this.participants.clear();
                    } else if (parse(words[0]) == this.joinedBattle) {
                        this.participants.remove(words[1]);
                    }
                    break;
                }
                changedBattles();
                this.listener.onBattleRoomChanged();
                return;
            case "JOINBATTLE":
                synchronized (this) {
                    this.joinedBattle = parse(words[0]);
                    this.pendingBattle = -1;
                    this.participants.clear();
                    this.startBoxes.clear();
                    this.modOptions.clear();
                    this.skills.clear();
                    Battle battle4 = this.battles.get(Integer.valueOf(this.joinedBattle));
                    if (battle4 != null) {
                        for (String name2 : battle4.users) {
                            this.participants.put(name2, new Participant(name2, null));
                        }
                    }
                    this.ready = false;
                    this.joinQueue.clear();
                    Battle joined = this.battles.get(Integer.valueOf(this.joinedBattle));
                    User founder = joined != null ? this.users.get(joined.founder) : null;
                    if (founder != null && founder.inGame()) {
                        this.wantPlayer = false;
                    }
                    break;
                }
                send("c.battle.queue_status");
                tellHostParty();
                this.listener.onBattleRoomChanged();
                return;
            case "s.battle.update_lobby_title":
                String[] parts = rest.split("\t", 2);
                synchronized (this) {
                    Battle battle5 = this.battles.get(Integer.valueOf(parse(parts[0].trim())));
                    if (battle5 != null && parts.length > 1) {
                        battle5.title = parts[1].trim();
                    }
                    break;
                }
                changedBattles();
                this.listener.onBattleRoomChanged();
                return;
            case "s.party.invited_to_party":
            case "s.party.joined_party":
            case "s.party.left_party":
            case "s.party.invite_cancelled":
                if (words.length >= 2) {
                    partyEvent(command.substring("s.party.".length()), words[0], words[1]);
                    return;
                }
                return;
            case "JOINBATTLEFAILED":
                synchronized (this) {
                    this.pendingBattle = -1;
                    break;
                }
                this.listener.onState("Could not join the battle: " + rest);
                return;
            case "FORCEQUITBATTLE":
                this.listener.onState("You were removed from the battle");
                return;
            case "REQUESTBATTLESTATUS":
                sendBattleStatus();
                return;
            case "CLIENTBATTLESTATUS":
                synchronized (this) {
                    Participant participant = this.participants.computeIfAbsent(words[0], name3 -> {
                        return new Participant(name3, null);
                    });
                    participant.battleStatus = parse(words[1]);
                    if (words.length > 2) {
                        participant.colour = parse(words[2]);
                    }
                    if (words[0].equalsIgnoreCase(this.userName) && participant.player() && !this.wantPlayer) {
                        this.wantPlayer = true;
                    }
                    break;
                }
                askQueue();
                this.listener.onBattleRoomChanged();
                return;
            case "s.battle.queue_status":
                String[] parts2 = rest.split("\t");
                synchronized (this) {
                    if (parse(parts2[0].trim()) == this.joinedBattle) {
                        this.joinQueue.clear();
                        for (int i = 1; i < parts2.length; i++) {
                            if (!parts2[i].isBlank()) {
                                this.joinQueue.add(parts2[i].trim());
                            }
                        }
                        if (queuePosition() > 0) {
                            this.wantPlayer = false;
                        }
                    }
                    break;
                }
                this.listener.onBattleRoomChanged();
                return;
            case "ADDBOT":
                synchronized (this) {
                    if (parse(words[0]) == this.joinedBattle && words.length >= 4) {
                        Participant bot = new Participant(words[1], words[2]);
                        bot.battleStatus = parse(words[3]);
                        if (words.length > 4) {
                            bot.colour = parse(words[4]);
                        }
                        this.participants.put(words[1], bot);
                    }
                    break;
                }
                this.listener.onBattleRoomChanged();
                return;
            case "UPDATEBOT":
                synchronized (this) {
                    Participant bot2 = this.participants.get(words[1]);
                    if (parse(words[0]) == this.joinedBattle && bot2 != null) {
                        bot2.battleStatus = parse(words[2]);
                        if (words.length > 3) {
                            bot2.colour = parse(words[3]);
                        }
                    }
                    break;
                }
                this.listener.onBattleRoomChanged();
                return;
            case "REMOVEBOT":
                synchronized (this) {
                    if (parse(words[0]) == this.joinedBattle) {
                        this.participants.remove(words[1]);
                    }
                    break;
                }
                this.listener.onBattleRoomChanged();
                return;
            case "SETSCRIPTTAGS":
                synchronized (this) {
                    for (String tag : rest.split("\t")) {
                        int equals = tag.indexOf(61);
                        String lower = tag.toLowerCase(Locale.ROOT);
                        if (equals > 0 && lower.startsWith("game/players/") && lower.substring(0, equals).endsWith("/skill")) {
                            String number = tag.substring(equals + 1).replaceAll("[^0-9.-]", "");
                            this.skills.put(tag.substring("game/players/".length(), equals - "/skill".length()), number);
                        }
                        if (equals > 0 && tag.toLowerCase(Locale.ROOT).startsWith("game/modoptions/")) {
                            this.modOptions.put(tag.substring("game/modoptions/".length(), equals).toLowerCase(Locale.ROOT), tag.substring(equals + 1));
                        }
                    }
                    break;
                }
                this.listener.onBattleRoomChanged();
                return;
            case "REMOVESCRIPTTAGS":
                synchronized (this) {
                    for (String tag2 : words) {
                        if (tag2.toLowerCase(Locale.ROOT).startsWith("game/modoptions/")) {
                            this.modOptions.remove(tag2.substring("game/modoptions/".length()).toLowerCase(Locale.ROOT));
                        }
                    }
                    break;
                }
                this.listener.onBattleRoomChanged();
                return;
            case "ADDSTARTRECT":
                synchronized (this) {
                    if (words.length >= 5) {
                        this.startBoxes.put(Integer.valueOf(parse(words[0])), new float[]{parse(words[1]) / 200.0f, parse(words[2]) / 200.0f, parse(words[3]) / 200.0f, parse(words[4]) / 200.0f});
                    }
                    break;
                }
                this.listener.onBattleRoomChanged();
                return;
            case "REMOVESTARTRECT":
                synchronized (this) {
                    this.startBoxes.remove(Integer.valueOf(parse(words[0])));
                    break;
                }
                this.listener.onBattleRoomChanged();
                return;
            case "SAIDBATTLE":
            case "SAIDBATTLEEX":
                String[] parts3 = rest.split(" ", 2);
                if (parts3.length > 1 && (parts3[1].startsWith("$joinq") || parts3[1].startsWith("$leaveq"))) {
                    askQueue();
                    return;
                }
                if (parts3.length > 1 && parts3[0].equals("Coordinator") && parts3[1].contains("queue")) {
                    askQueue();
                }
                this.listener.onBattleChat(parts3[0], parts3.length > 1 ? parts3[1] : "", command.endsWith("EX"));
                return;
            case "JOIN":
                synchronized (this) {
                    this.channels.computeIfAbsent(words[0], name4 -> {
                        return new TreeSet(String.CASE_INSENSITIVE_ORDER);
                    });
                    break;
                }
                return;
            case "CLIENTS":
                synchronized (this) {
                    Set<String> members = this.channels.computeIfAbsent(words[0], name5 -> {
                        return new TreeSet(String.CASE_INSENSITIVE_ORDER);
                    });
                    for (int i2 = 1; i2 < words.length; i2++) {
                        members.add(words[i2]);
                    }
                    break;
                }
                changedUsers();
                return;
            case "JOINED":
            case "LEFT":
                synchronized (this) {
                    Set<String> members2 = this.channels.get(words[0]);
                    if (members2 != null && words.length > 1) {
                        if (command.equals("JOINED")) {
                            members2.add(words[1]);
                        } else {
                            members2.remove(words[1]);
                        }
                    }
                    break;
                }
                changedUsers();
                return;
            case "SAID":
            case "SAIDEX":
                String[] parts4 = rest.split(" ", 3);
                if (parts4.length == 3) {
                    this.listener.onChat(parts4[0], parts4[1], parts4[2], command.endsWith("EX"));
                    return;
                }
                return;
            case "RING":
                this.listener.onRing(words.length > 0 ? words[0] : "");
                return;
            case "SAIDPRIVATE":
                String[] parts5 = rest.split(" ", 2);
                if (parts5.length <= 1 || !parts5[1].startsWith("!party")) {
                    this.listener.onPrivateChat(parts5[0], parts5.length > 1 ? parts5[1] : "", false);
                    return;
                }
                return;
            case "SAYPRIVATE":
                String[] parts6 = rest.split(" ", 2);
                if (parts6.length <= 1 || (!parts6[1].startsWith("!party") && !parts6[1].startsWith("!redeem"))) {
                    this.listener.onPrivateChat(parts6[0], parts6.length > 1 ? parts6[1] : "", true);
                    return;
                }
                return;
            case "FRIENDLISTBEGIN":
                synchronized (this) {
                    this.friends.clear();
                    break;
                }
                return;
            case "FRIENDLIST":
                synchronized (this) {
                    this.friends.add(rest.replace("userName=", "").trim());
                    break;
                }
                return;
            case "FRIENDREQUESTLISTBEGIN":
                synchronized (this) {
                    this.friendRequests.clear();
                    break;
                }
                return;
            case "FRIENDREQUESTLIST":
                synchronized (this) {
                    this.friendRequests.add(rest.replace("userName=", "").split("\t")[0].trim());
                    break;
                }
                return;
            case "FRIENDLISTEND":
            case "FRIENDREQUESTLISTEND":
                this.listener.onFriendsChanged();
                return;
            case "SERVERMSG":
            case "SERVERMSGBOX":
                this.listener.onChat("main", "server", rest, true);
                return;
            default:
                return;
        }
    }

    private void fail(String message) {
        Socket connection;
        synchronized (this) {
            connection = this.socket;
        }
        closed(connection, message);
    }

    private void clientStatus(String[] words) {
        Battle started = null;
        Battle ended = null;
        boolean unready = false;
        synchronized (this) {
            User user = this.users.get(words[0]);
            if (user == null) {
                return;
            }
            boolean wasInGame = user.inGame();
            user.status = parse(words[1]);
            Battle mine = this.battles.get(Integer.valueOf(this.joinedBattle));
            boolean founder = mine != null && mine.founder.equalsIgnoreCase(user.name);
            if (!wasInGame && user.inGame() && founder) {
                started = mine;
            } else if (wasInGame && !user.inGame() && founder) {
                ended = mine;
                unready = this.ready;
                this.ready = false;
            }
            String password = this.scriptPassword;
            if (unready) {
                sendBattleStatus();
            }
            changedUsers();
            changedBattles();
            if (started != null) {
                this.listener.onGameStart(started, password);
            }
            if (ended != null) {
                this.listener.onGameEnd(ended);
            }
        }
    }

    private void battleOpened(String rest, String[] words) {
        String[] fields = rest.split("\t");
        if (words.length < 10 || fields.length < 5) {
            return;
        }
        Battle battle = new Battle(parse(words[0]), words[3], words[4], parse(words[5]), "1".equals(words[7]), fields[1], fields[2], fields[3], fields[4]);
        battle.users.add(words[3]);
        synchronized (this) {
            this.battles.put(Integer.valueOf(battle.id), battle);
        }
        changedBattles();
    }

    private void roomChanged(int battleId) {
        boolean mine;
        synchronized (this) {
            mine = battleId == this.joinedBattle;
        }
        if (mine) {
            this.listener.onBattleRoomChanged();
        }
    }

    private void changedUsers() {
        if (isLoggedIn()) {
            this.listener.onUsersChanged();
        }
    }

    private void changedBattles() {
        if (isLoggedIn()) {
            this.listener.onBattlesChanged();
        }
    }

    private static int parse(String number) {
        try {
            return Integer.parseInt(number.trim());
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    synchronized boolean isConnected() {
        return this.socket != null;
    }

    synchronized boolean isLoggedIn() {
        return this.loggedIn;
    }

    synchronized long lastSessionMillis() {
        return this.lastSessionMillis;
    }

    synchronized String getUserName() {
        return this.userName;
    }

    synchronized List<User> getUsers() {
        return new ArrayList(this.users.values());
    }

    synchronized User getUser(String name) {
        return this.users.get(name);
    }

    synchronized List<Battle> getBattles() {
        return new ArrayList(this.battles.values());
    }

    synchronized Battle getJoinedBattle() {
        return this.battles.get(Integer.valueOf(this.joinedBattle));
    }

    synchronized List<Participant> getParticipants() {
        return new ArrayList(this.participants.values());
    }

    synchronized Map<Integer, float[]> getStartBoxes() {
        return new TreeMap(this.startBoxes);
    }

    synchronized Map<String, String> getModOptions() {
        return new TreeMap(this.modOptions);
    }

    synchronized List<String> getFriends() {
        return new ArrayList(this.friends);
    }

    synchronized List<String> getFriendRequests() {
        return new ArrayList(this.friendRequests);
    }

    synchronized String getScriptPassword() {
        return this.scriptPassword;
    }

    synchronized boolean isReady() {
        return this.ready;
    }

    synchronized boolean isPlayer() {
        return this.wantPlayer;
    }

    void confirmAgreement(String code) {
        String name;
        send("CONFIRMAGREEMENT" + ((code == null || code.isBlank()) ? "" : " " + code.trim()));
        synchronized (this) {
            name = this.userName;
        }
        this.listener.onState("Agreement confirmed, sign in again as " + name);
    }

    void say(String channel, String message) {
        if (!message.isBlank()) {
            send("SAY " + channel + " " + clean(message));
        }
    }

    void joinChannel(String channel) {
        send("JOIN " + channel);
    }

    void leaveChannel(String channel) {
        send("LEAVE " + channel);
        synchronized (this) {
            this.channels.remove(channel);
        }
    }

    void sayPrivate(String user, String message) {
        if (!message.isBlank()) {
            send("SAYPRIVATE " + user + " " + clean(message));
        }
    }

    void sayBattle(String message) {
        if (!message.isBlank()) {
            send("SAYBATTLE " + clean(message));
        }
    }

    void joinBattle(int id, String password) {
        synchronized (this) {
            if (this.joinedBattle == id || (this.pendingBattle == id && System.currentTimeMillis() - this.pendingSince < 6000)) {
                return;
            }
            this.pendingSince = System.currentTimeMillis();
            if (this.joinedBattle >= 0) {
                send("LEAVEBATTLE");
            }
            this.pendingBattle = id;
            this.scriptPassword = UUID.randomUUID().toString().replace("-", "").substring(0, 12);
            this.wantPlayer = true;
            this.wantAlly = -1;
            this.synced = false;
            this.lastSentStatus = -1;
            send("JOINBATTLE " + id + " " + ((password == null || password.isBlank()) ? "*" : password.trim()) + " " + this.scriptPassword);
        }
    }

    void setStatus(boolean inGame, boolean away) {
        int status = (inGame ? 1 : 0) | (away ? 2 : 0);
        synchronized (this) {
            if (!this.loggedIn || status == this.sentStatus) {
                return;
            }
            this.sentStatus = status;
            send("MYSTATUS " + status);
        }
    }

    void leaveBattle() {
        send("LEAVEBATTLE");
    }

    void setSide(int side) {
        synchronized (this) {
            this.wantSide = side;
        }
        sendBattleStatus();
    }

    void setColour(int colour) {
        synchronized (this) {
            this.wantColour = colour;
        }
        sendBattleStatus();
    }

    synchronized int getColour() {
        return this.wantColour;
    }

    synchronized int getSide() {
        return this.wantSide;
    }

    synchronized String getSkill(String user) {
        return this.skills.getOrDefault(user, "");
    }

    void setReady(boolean value) {
        synchronized (this) {
            this.ready = value;
        }
        sendBattleStatus();
    }

    void setAllyTeam(int allyTeam) {
        synchronized (this) {
            this.wantPlayer = true;
            this.wantAlly = allyTeam;
        }
        sendBattleStatus();
    }

    void setPlayer(boolean value) {
        synchronized (this) {
            this.wantPlayer = value;
            this.ready = value && this.ready;
        }
        sendBattleStatus();
    }

    void setSynced(boolean value) {
        boolean changed;
        synchronized (this) {
            changed = this.synced != value;
            this.synced = value;
        }
        if (changed) {
            sendBattleStatus();
        }
    }

    private void sendBattleStatus() {
        synchronized (this) {
            if (this.joinedBattle < 0) {
                return;
            }
            Participant me = this.participants.get(this.userName);
            int assigned = me != null ? me.battleStatus & 1020 : 0;
            if (this.wantAlly >= 0) {
                assigned = (assigned & 60) | ((this.wantAlly & 15) << 6);
            }
            int status = assigned | (this.wantPlayer ? 1024 : 0) | ((this.ready && this.wantPlayer) ? 2 : 0) | (this.synced ? 4194304 : 8388608) | ((this.wantSide & 15) << 24);
            synchronized (this) {
                if (this.wantColour == 16744192 && this.joinedBattle >= 0) {
                    this.wantColour = freeColour();
                }
                int colour = this.wantColour;
                if (status == this.lastSentStatus && colour == this.lastSentColour && System.currentTimeMillis() - this.lastStatusTime < 30000) {
                    return;
                }
                this.lastSentStatus = status;
                this.lastSentColour = colour;
                this.lastStatusTime = System.currentTimeMillis();
                send("MYBATTLESTATUS " + status + " " + colour);
            }
        }
    }

    /* JADX INFO: loaded from: app_classes.zip:com/faforever/neroxis/generator/bar/LobbyClient$AiKind.class */
    static final class AiKind extends Record {
        private final String label;
        private final String library;
        private final String prefix;

        AiKind(String label, String library, String prefix) {
            this.label = label;
            this.library = library;
            this.prefix = prefix;
        }

        @Override // java.lang.Record
        public final String toString() {
            return (String) ObjectMethods.bootstrap(MethodHandles.lookup(), "toString", MethodType.methodType(String.class, AiKind.class), AiKind.class, "label;library;prefix", "FIELD:Lcom/faforever/neroxis/generator/bar/LobbyClient$AiKind;->label:Ljava/lang/String;", "FIELD:Lcom/faforever/neroxis/generator/bar/LobbyClient$AiKind;->library:Ljava/lang/String;", "FIELD:Lcom/faforever/neroxis/generator/bar/LobbyClient$AiKind;->prefix:Ljava/lang/String;").dynamicInvoker().invoke(this) /* invoke-custom */;
        }

        @Override // java.lang.Record
        public final int hashCode() {
            return (int) ObjectMethods.bootstrap(MethodHandles.lookup(), "hashCode", MethodType.methodType(Integer.TYPE, AiKind.class), AiKind.class, "label;library;prefix", "FIELD:Lcom/faforever/neroxis/generator/bar/LobbyClient$AiKind;->label:Ljava/lang/String;", "FIELD:Lcom/faforever/neroxis/generator/bar/LobbyClient$AiKind;->library:Ljava/lang/String;", "FIELD:Lcom/faforever/neroxis/generator/bar/LobbyClient$AiKind;->prefix:Ljava/lang/String;").dynamicInvoker().invoke(this) /* invoke-custom */;
        }

        @Override // java.lang.Record
        public final boolean equals(Object o) {
            return (boolean) ObjectMethods.bootstrap(MethodHandles.lookup(), "equals", MethodType.methodType(Boolean.TYPE, AiKind.class, Object.class), AiKind.class, "label;library;prefix", "FIELD:Lcom/faforever/neroxis/generator/bar/LobbyClient$AiKind;->label:Ljava/lang/String;", "FIELD:Lcom/faforever/neroxis/generator/bar/LobbyClient$AiKind;->library:Ljava/lang/String;", "FIELD:Lcom/faforever/neroxis/generator/bar/LobbyClient$AiKind;->prefix:Ljava/lang/String;").dynamicInvoker().invoke(this, o) /* invoke-custom */;
        }

        public String label() {
            return this.label;
        }

        public String library() {
            return this.library;
        }

        public String prefix() {
            return this.prefix;
        }
    }

    void addBot(int allyTeam) {
        addBot(allyTeam, AI_KINDS.get(1));
    }

    void addBot(int allyTeam, AiKind kind) {
        synchronized (this) {
            if (this.joinedBattle < 0) {
                return;
            }
            Set<Integer> usedTeams = new HashSet<>();
            for (Participant participant : this.participants.values()) {
                if (participant.player()) {
                    usedTeams.add(Integer.valueOf(participant.team()));
                }
            }
            int team = 0;
            while (usedTeams.contains(Integer.valueOf(team)) && team < 15) {
                team++;
            }
            int number = 1;
            while (this.participants.containsKey(kind.prefix() + "(" + number + ")")) {
                number++;
            }
            String name = kind.prefix() + "(" + number + ")";
            int status = 2 | (team << 2) | ((allyTeam & 15) << 6) | 1024 | 4194304;
            Participant bot = new Participant(name, this.userName);
            bot.battleStatus = status;
            this.participants.put(name, bot);
            this.listener.onBattleRoomChanged();
            int colour = freeColour();
            send("ADDBOT " + name + " " + status + " " + colour + " " + kind.library());
        }
    }

    private synchronized int freeColour() {
        Set<Integer> taken = new HashSet<>();
        taken.add(Integer.valueOf(this.wantColour));
        this.participants.values().forEach(participant -> {
            taken.add(Integer.valueOf(participant.colour));
        });
        int start = Math.abs((this.userName != null ? this.userName : "").toLowerCase(Locale.ROOT).hashCode()) % PlayerColors.FAF.size();
        for (int i = 0; i < PlayerColors.FAF.size(); i++) {
            PlayerColors.Named named = PlayerColors.FAF.get((start + i) % PlayerColors.FAF.size());
            int candidate = PlayerColors.toProtocol(named.color());
            if (!taken.contains(Integer.valueOf(candidate))) {
                return candidate;
            }
        }
        for (PlayerColors.Named named2 : PlayerColors.FAF) {
            int candidate2 = PlayerColors.toProtocol(named2.color());
            if (!taken.contains(Integer.valueOf(candidate2))) {
                return candidate2;
            }
        }
        return new Random().nextInt(16777215);
    }

    void removeBot(String name) {
        send("REMOVEBOT " + name);
    }

    private void partyEvent(String event, String party, String user) {
        boolean tellHost = false;
        List<String> invites = List.of();
        synchronized (this) {
            boolean me = user.equalsIgnoreCase(this.userName);
            boolean mine = party.equals(this.partyId);
            switch (event) {
                case "invited_to_party":
                    if (me) {
                        this.partyInvitations.putIfAbsent(party, new TreeSet(String.CASE_INSENSITIVE_ORDER));
                        break;
                    } else {
                        if (mine) {
                            this.partyInvited.add(user);
                        }
                        break;
                    }
                    break;
                case "joined_party":
                    if (me) {
                        this.partyId = party;
                        Set<String> known = this.partyInvitations.remove(party);
                        this.partyMembers.clear();
                        this.partyInvited.clear();
                        if (known != null) {
                            this.partyMembers.addAll(known);
                        }
                        this.partyMembers.add(user);
                        invites = new ArrayList<>(this.partyInvitesToSend);
                        this.partyInvitesToSend.clear();
                        tellHost = true;
                        break;
                    } else {
                        if (mine) {
                            this.partyMembers.add(user);
                            this.partyInvited.remove(user);
                        } else if (this.partyInvitations.containsKey(party)) {
                            this.partyInvitations.get(party).add(user);
                        }
                        break;
                    }
                    break;
                case "left_party":
                    if (me && mine) {
                        this.partyId = null;
                        this.partyMembers.clear();
                        this.partyInvited.clear();
                        tellHost = true;
                        break;
                    } else {
                        if (mine) {
                            this.partyMembers.remove(user);
                        } else if (this.partyInvitations.containsKey(party)) {
                            this.partyInvitations.get(party).remove(user);
                            if (this.partyInvitations.get(party).isEmpty()) {
                                this.partyInvitations.remove(party);
                            }
                        }
                        break;
                    }
                    break;
                default:
                    if (me) {
                        this.partyInvitations.remove(party);
                        break;
                    } else {
                        if (mine) {
                            this.partyInvited.remove(user);
                        }
                        break;
                    }
                    break;
            }
        }
        invites.forEach(name -> {
            send("c.party.invite_to_party " + name);
        });
        if (tellHost) {
            tellHostParty();
        }
        this.listener.onPartyChanged();
    }

    private void tellHostParty() {
        String founder;
        String party;
        synchronized (this) {
            Battle battle = this.battles.get(Integer.valueOf(this.joinedBattle));
            founder = battle != null ? battle.founder : null;
            party = this.partyId;
        }
        if (founder != null) {
            send("SAYPRIVATE " + founder + " !party " + (party != null ? party : "-"));
        }
    }

    void createParty() {
        boolean create;
        synchronized (this) {
            create = this.partyId == null;
        }
        if (create) {
            send("c.party.create_new_party");
        }
    }

    void inviteToParty(String user) {
        boolean create;
        synchronized (this) {
            create = this.partyId == null;
            if (create) {
                this.partyInvitesToSend.add(user);
            }
        }
        send(create ? "c.party.create_new_party" : "c.party.invite_to_party " + user);
    }

    /* JADX WARN: Code duplicated, block: B:12:0x001a A[Catch: all -> 0x002b, TryCatch #0 {, blocks: (B:6:0x0009, B:12:0x001a, B:14:0x0027), top: B:30:0x0009 }] */
    /* JADX WARN: Code duplicated, block: B:9:0x0014  */
    void answerPartyInvitation(String party, boolean accept) {
        boolean z;
        boolean leaveFirst;
        synchronized (this) {
            if (accept) {
                if (this.partyId != null) {
                    z = true;
                } else {
                    z = false;
                }
                leaveFirst = z;
                if (!accept) {
                    this.partyInvitations.remove(party);
                }
            } else {
                z = false;
                leaveFirst = z;
                if (!accept) {
                    this.partyInvitations.remove(party);
                }
            }
            throw th;
        }
        if (leaveFirst) {
            send("c.party.leave_current_party");
        }
        send((accept ? "c.party.accept_invite_to_party " : "c.party.decline_invite_to_party ") + party);
        this.listener.onPartyChanged();
    }

    void cancelPartyInvitation(String user) {
        synchronized (this) {
            this.partyInvited.remove(user);
        }
        send("c.party.cancel_invite_to_party " + user);
        this.listener.onPartyChanged();
    }

    void leaveParty() {
        send("c.party.leave_current_party");
        synchronized (this) {
            this.partyId = null;
            this.partyMembers.clear();
            this.partyInvited.clear();
        }
        tellHostParty();
        this.listener.onPartyChanged();
    }

    synchronized List<String> getPartyMembers() {
        return new ArrayList(this.partyMembers);
    }

    synchronized List<String> getPartyInvited() {
        return new ArrayList(this.partyInvited);
    }

    synchronized Map<String, List<String>> getPartyInvitations() {
        Map<String, List<String>> result = new LinkedHashMap<>();
        this.partyInvitations.forEach((party, members) -> {
            result.put(party, new ArrayList(members));
        });
        return result;
    }

    boolean redeem(String code) {
        String founder;
        synchronized (this) {
            Battle battle = this.battles.get(Integer.valueOf(this.joinedBattle));
            if (battle == null) {
                battle = this.battles.values().stream().findFirst().orElse(null);
            }
            founder = battle != null ? battle.founder : null;
        }
        if (founder == null) {
            return false;
        }
        send("SAYPRIVATE " + founder + " !redeem " + clean(code));
        return true;
    }

    boolean report(String user, String reason) {
        String founder;
        synchronized (this) {
            Battle battle = this.battles.get(Integer.valueOf(this.joinedBattle));
            if (battle == null) {
                battle = this.battles.values().stream().findFirst().orElse(null);
            }
            founder = battle != null ? battle.founder : null;
        }
        if (founder == null) {
            return false;
        }
        send("SAYPRIVATE " + founder + " !report " + user + " " + clean(reason));
        return true;
    }

    void ring(String user) {
        send("RING " + user);
    }

    void ignore(String user) {
        send("IGNORE userName=" + user);
    }

    void refreshFriends() {
        send("FRIENDLIST");
        send("FRIENDREQUESTLIST");
    }

    void requestFriend(String user) {
        send("FRIENDREQUEST userName=" + user.trim());
        refreshFriends();
    }

    void answerFriendRequest(String user, boolean accept) {
        send((accept ? "ACCEPTFRIENDREQUEST" : "DECLINEFRIENDREQUEST") + " userName=" + user);
        refreshFriends();
    }

    void removeFriend(String user) {
        send("UNFRIEND userName=" + user);
        refreshFriends();
    }

    private static String clean(String message) {
        return message.replace('\n', ' ').replace('\r', ' ').trim();
    }
}
