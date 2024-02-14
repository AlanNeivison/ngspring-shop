import { Component } from '@angular/core';
import { AdminService } from '../../service/admin.service';
import { MatSnackBar } from '@angular/material/snack-bar';

@Component({
  selector: 'app-orders',
  templateUrl: './orders.component.html',
  styleUrl: './orders.component.scss'
})
export class OrdersComponent {
  orders: any;
  constructor(private adminService: AdminService,
    private matSnackBar: MatSnackBar) { }

  ngOnInit() {
    this.getPlacedOrders();
  }

  getPlacedOrders() {
    this.adminService.getPlacedOrders().subscribe(res => {
      this.orders = res;
    }, error => {
      console.error('Error fetching placed orders:', error);
      this.matSnackBar.open('Error fetching placed orders', 'Close', {
        duration: 3000,
        panelClass: 'snackbar-error'
      });
    }
    );
  }

}
