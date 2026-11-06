package model;
import java.time.format.*;
import java.time.*;
public class Transactions {
    private final int MAX_BORROW_DAY = 14; //muon duoc toi da 1 lan 14 sach
    private final double FINE_PER_DAY = 5000.0; //phat 5k chop moi ngay muon han tra sach
    private String transactionID, borrowID, status;
    private LocalDate borrowDate, dueDate, returnDate;
    private double fineAmount;

}
