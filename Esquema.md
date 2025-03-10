## Gateway

### Data structures
    Set<Client> clients
    Set<Downloader> downloaders
    Queue<String> urls  (thread safe)
    Set<Pair<Int, String>> mostLookedUpWord
    Set<Pair<Int, String>> mostLookedUpURL
	Set<PairBarrelClock> bufferACK;
	Map<PairBarrelClock, ArrayList<String>> bufferPals;
	Map<PairBarrelClock, PairIndRecParents> bufferSync;
	Set<PairBarrelClock> sendBuffer;

### Auxiliary structures
    PairClockFlag: Clock -> Stamp for comunications, Flag -> indicates barrel type and sinchronization state
    PairBarrelClock: Barrel and its Clock (either true or expected, depending on the use of this structure)
    PairIndRecParents: IndRec -> Recursive Index, Parents -> Parents of all URLs

### Subscribe function for all classes
    void subscribe(Client)
    void subscribe(Downloader)
    void subscribe(Barrel)

### Functions used by downloaders
    String takeURL()
    void putURL(String)
    void receiveURLChildren(String, Array<String)
    void receiveIndex(String, Array<String>)

### Functions used by Barrels
    acknoledgeUpdate(Barrel, int clock) 
    queryResponse(ArrayList<String> top10, Barrel, int clock)
    syncResponse(Map<String, Set<String>> indRec, Map<String, Set<String>> parents, Barrel, int clock)

### Functions used by Clients
    Array<String> queryWord(String, Int)

## Client

### Variables
    Gateway gateway
    
### Functions used by Gateway
    void receiveTop10Words()
    void receiveTop10URL()

## Downloader

### Variables
    Gateway gateway

## Barrel

### Data structures
    String filename
    Gateway gateway
    HashMap<String, Set<String>> recIndex
    HashMap<String, Set<String>> parents

### Functions used by Gateway
    void syncRequest()
    void queryWord(String, Int)
    void update(Pair<String, ArrayList<String>>, Pair<String, ArrayList<String>>, int)

### Other Functions
    void retriveFormFile()
    
