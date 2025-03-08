## Gateway

### Data structures
    Set<Client> clients
    Set<Downloader> downloaders
    Set<BarrelAM, Integer> barrelsAM
    Set<BarrelNZ, Integer> barrelsNZ
    Queue<String> urls  (thread safe)
    Set<Pair<Int, String>> mostLookedUpWord
    Set<Pair<Int, String>> mostLookedUpURL

### Subscribe function for all classes
    void subscribe(Client)
    void subscribe(Downloader)
    void subscribe(BarrelAM)
    void subscribe(BarrelNZ)

### Functions used by downloaders
    String takeURL()
    void putURL(String)
    void receiveURLChildren(String, Array<String)
    void receiveIndex(String, Array<String>)

### Functions used by Barrels
    acknoledge(BarrelAM, int) 
    acknoledge(BarrelNZ, int) 
    queryResponse(ArrayList<String>, BarrelAM, int)
    queryResponse(ArrayList<String>, BarrelNZ, int)
    syncResponse(Map<String, Set<String>>, Map<String, Set<String>>, BarrelAM, int)
    syncResponse(Map<String, Set<String>>, Map<String, Set<String>>, BarrelNZ, int)

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
    
