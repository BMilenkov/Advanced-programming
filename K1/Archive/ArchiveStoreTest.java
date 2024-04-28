package K1.Archive;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;


class NonExistingItemException extends Exception {
    public NonExistingItemException(String line) {
        super(line);
    }
}

abstract class Archive {

    private int id;
    private LocalDate dateArchived;

    public Archive(int id) {
        this.id = id;
        this.dateArchived = null;
    }

    public void setDateArchived(LocalDate dateArchived) {
        this.dateArchived = dateArchived;
    }

    public int getId() {
        return id;
    }

    public LocalDate getDateArchived() {
        return dateArchived;
    }

    public abstract String openArchive(LocalDate date);

}

class LockedArchive extends Archive {
    private LocalDate dateToOpen;

    public LockedArchive(int id, LocalDate dateToOpen) {
        super(id);
        this.dateToOpen = dateToOpen;
    }

    @Override
    public String openArchive(LocalDate date) {
        if (date.isBefore(dateToOpen))
            return String.format("Item %d cannot be opened before %s", getId(), dateToOpen);
        return String.format("Item %d opened at %s", getId(), date);
    }
}


class SpecialArchive extends Archive {
    private int maxOpen;
    private int tries;

    public SpecialArchive(int id, int maxOpen) {
        super(id);
        this.maxOpen = maxOpen;
        this.tries = 0;
    }

    public void setTries(int tries) {
        this.tries = tries;
    }

    @Override
    public String openArchive(LocalDate date) {
        setTries(++tries);
        if (tries > maxOpen)
            return String.format("Item %d cannot be opened more than %d times",
                    getId(), maxOpen);
        return String.format("Item %d opened at %s", getId(), date);
    }
}


class ArchiveStore {
    private final List<Archive> archives;
    private final List<String> actions;

    public ArchiveStore() {
        archives = new ArrayList<>();
        actions = new ArrayList<>();
    }

    public void archiveItem(Archive item, LocalDate date) {
        archives.add(item);
        item.setDateArchived(date);
        actions.add(String.format("Item %d archived at %s",
                item.getId(), item.getDateArchived()));
    }

    void openItem(int id, LocalDate date)
            throws NonExistingItemException {

        if (archives.stream().noneMatch(item -> (item.getId() == id)))
            throw new NonExistingItemException
                    (String.format("Item with id %d doesn't exist", id));

        actions.add(archives.stream()
                .filter(archive -> archive.getId() == id)
                .findFirst().get().openArchive(date));
    }

    public String getLog() {
        StringBuilder sb = new StringBuilder();
        actions.forEach(action -> sb.append(action).append("\n"));
        return sb.toString();
    }
}


public class ArchiveStoreTest {
    public static void main(String[] args) {
        ArchiveStore store = new ArchiveStore();
        LocalDate date = LocalDate.of(2013, 10, 7);
        Scanner scanner = new Scanner(System.in);
        scanner.nextLine();
        int n = scanner.nextInt();
        scanner.nextLine();
        scanner.nextLine();
        int i;
        for (i = 0; i < n; ++i) {
            int id = scanner.nextInt();
            long days = scanner.nextLong();

            LocalDate dateToOpen = date.atStartOfDay().plusSeconds(days * 24 * 60 * 60).toLocalDate();
            LockedArchive lockedArchive = new LockedArchive(id, dateToOpen);
            store.archiveItem(lockedArchive, date);
        }
        scanner.nextLine();
        scanner.nextLine();
        n = scanner.nextInt();
        scanner.nextLine();
        scanner.nextLine();
        for (i = 0; i < n; ++i) {
            int id = scanner.nextInt();
            int maxOpen = scanner.nextInt();
            SpecialArchive specialArchive = new SpecialArchive(id, maxOpen);
            store.archiveItem(specialArchive, date);
        }
        scanner.nextLine();
        scanner.nextLine();
        while (scanner.hasNext()) {
            int open = scanner.nextInt();
            try {
                store.openItem(open, date);
            } catch (NonExistingItemException e) {
                System.out.println(e.getMessage());
            }
        }
        System.out.println(store.getLog());
    }
}