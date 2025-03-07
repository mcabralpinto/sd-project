## Gateway

### Data structures
    Set<Client> clients
    Set<Downloader> downloaders
    Set<Barrel> barrels
    Queue<String> urls  (thread safe)
    HashMap<Barrel, Set<String>> lostInfo
    Set<Pair<Int, String>> mostLookedUpWord
    Set<Pair<Int, String>> mostLookedUpURL

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
    T[^data] receiveLostData(Barrel)


### Functions used by Clients
    Array<String> queryWord(String, Int[^pages])

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
    HashMap<String, Array<String>> index
    HashMap<String, Array<String>> children

### Functions used by Gateway
    T[^data] pedeInfoPerdida(String)
    [^data]: Dados a serem sincronizados
    Array<String> queryWord(String, Int[^pages])
    [^pages] número da página de URLs (cada página tem 1o URLs)

### Other Functions
    void retriveFormFile()
    
