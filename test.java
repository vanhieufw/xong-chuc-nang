package com.movie.ui;

import com.movie.bus.MovieBUS; import com.movie.bus.RoomBUS; import com.movie.bus.ShowtimeBUS; import com.movie.bus.StaffBUS; import com.movie.bus.CustomerBUS; import com.movie.bus.RevenueBUS; import com.movie.model.Movie; import com.movie.model.Room; import com.movie.model.Showtime; import com.movie.model.Staff; import com.movie.model.Customer; import com.movie.network.ThreadManager;

import javax.swing.; import java.awt.; import java.sql.SQLException; import java.text.SimpleDateFormat; import java.util.Date; import java.util.List; import java.util.concurrent.atomic.AtomicBoolean;

public class AdminFrame extends JFrame { private JPanel mainPanel; private CardLayout cardLayout; private JPanel contentPanel; private MovieBUS movieBUS = new MovieBUS(); private RoomBUS roomBUS = new RoomBUS(); private ShowtimeBUS showtimeBUS = new ShowtimeBUS(); private StaffBUS staffBUS = new StaffBUS(); private CustomerBUS customerBUS = new CustomerBUS(); private RevenueBUS revenueBUS = new RevenueBUS(); private JLabel timeLabel;

    public AdminFrame() { initUI(); startClock(); startShowtimeStatusUpdater(); }

    private void initUI() { setTitle("Quản lý bán vé xem phim - Admin"); setSize(1200, 800); setDefaultCloseOperation(DISPOSE_