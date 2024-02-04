import {Component, OnInit} from '@angular/core';
import {CustomerDto} from "../../models/customer-dto";
import {CustomerService} from "../../services/customer/customer.service";
import {CustomerRegistrationRequest} from "../../models/customer-registration-request";
import {ConfirmationService, MessageService} from "primeng/api";

@Component({
  selector: 'app-customer',
  templateUrl: './customer.component.html',
  styleUrl: './customer.component.scss'
})
export class CustomerComponent implements OnInit{
  display = false;
  operation: 'create' | 'update' = 'create';
  customers: Array<CustomerDto> = [];
  customer: CustomerRegistrationRequest = {};

  constructor(
    private readonly customerService: CustomerService,
    private readonly messageService: MessageService,
    private readonly confirmationService: ConfirmationService
  ) {}

  ngOnInit(): void {
    this.findAllCustomers();
  }

  private findAllCustomers() {
    this.customerService.findAll()
      .subscribe({
        next: (data) => {
          this.customers = data;
          console.log(data);
        }
      })
  }

  save(customer: CustomerRegistrationRequest){
    if(customer) {
      if(this.operation === 'create'){
      this.customerService.registerCustomer(customer)
        .subscribe({
          next: () => {
            this.findAllCustomers();
            this.display = false;
            this.customer = {};
            this.showSuccess(
              'Customer saved',
              `Customer ${customer.name} was successfully saved`
            );
          }
        });
      } else if(this.operation === 'update') {
        this.customerService.updateCustomer(customer.id, customer)
          .subscribe({
            next: () => {
              this.findAllCustomers();
              this.display = false;
              this.customer = {};
              this.showSuccess(
                'Customer updated',
                `Customer ${customer.name} was successfully updated`
              );
            }
          });
      }
    }
  }

  deleteCustomer(customer: CustomerDto) {
    this.confirmationService.confirm({
      header: 'Delete customer',
      message: `Are you sure you want to delete ${customer.name} ? You can\'t undo this action afterwards`,
      accept: () => {
        this.customerService.deleteCustomer(customer.id)
          .subscribe({
            next: () => {
              this.findAllCustomers();
              this.showSuccess('Customer deleted', `Customer ${customer.name} was successfully deleted`);
            }
          })
      }
    });
  }

  showSuccess(summary: string | undefined, message: string | undefined) {
    this.messageService.add({
      severity: 'success',
      summary: summary,
      detail: message
    });
  }

  updateCustomer(customer: CustomerDto) {
    this.display = true;
    this.customer = customer;
    this.operation = 'update';
  }

  createCustomer() {
    this.display = true;
    this.customer = {};
    this.operation = 'create';
  }

  cancel() {
    this.display = false;
    this.customer = {};
    this.operation = 'create';
  }
}
