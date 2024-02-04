import {Component, EventEmitter, Input, Output} from '@angular/core';
import {CardModule} from "primeng/card";
import {BadgeModule} from "primeng/badge";
import {ButtonModule} from "primeng/button";
import {CustomerDto} from "../../models/customer-dto";

@Component({
  selector: 'app-customer-card',
  standalone: true,
  imports: [
    CardModule,
    BadgeModule,
    ButtonModule
  ],
  templateUrl: './customer-card.component.html',
  styleUrl: './customer-card.component.scss'
})
export class CustomerCardComponent {

  @Input()
  customer: CustomerDto = {};
  @Input()
  customerIndex: number = 0;
  @Output()
  delete: EventEmitter<CustomerDto> = new EventEmitter<CustomerDto>();
  @Output()
  update: EventEmitter<CustomerDto> = new EventEmitter<CustomerDto>();

  get customerImage(): string {
    const gender = this.customer.gender === 'MALE' ? 'men' : 'women';
    return `https://randomuser.me/api/portraits/${gender}/${this.customerIndex}.jpg`;
  }

  onDelete() {
    this.delete.emit(this.customer);
  }

  onUpdate() {
    this.update.emit(this.customer);
  }
}
