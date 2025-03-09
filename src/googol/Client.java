package googol;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.util.Scanner;

public class Client {

    public Client() throws RemoteException {}

    public static void main(String args[]) {
        try {
            Index index = (Index) LocateRegistry.getRegistry(8183).lookup("index");
            while (true) {
                System.out.println("Enter an URL or search word:");
                Scanner scanner = new Scanner(System.in);
                String input = scanner.nextLine();
                index.handleRequest(input);
                scanner.close();
            }
        } catch (RemoteException | NotBoundException e) {
            e.printStackTrace();
        }
    }
}

// https://pt.wikipedia.org/wiki/Wikipédia:Página_principal
// https://pt.wikipedia.org/wiki/Wikip%C3%A9dia:P%C3%A1gina_principal
