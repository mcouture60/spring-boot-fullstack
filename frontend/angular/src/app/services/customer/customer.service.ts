import { Injectable } from '@angular/core';
import {HttpClient, HttpHeaders} from "@angular/common/http";
import {Observable} from "rxjs";
import {CustomerDto} from "../../models/customer-dto";
import {environment} from "../../../environments/environment";
import {CustomerRegistrationRequest} from "../../models/customer-registration-request";
import {CustomerUpdateRequest} from "../../models/customer-update-request";

@Injectable({
  providedIn: 'root'
})
export class CustomerService {

  private readonly customerUrl =
    `${environment.api.baseUrl}/${environment.api.customerUrl}`;

  constructor(
    private readonly http: HttpClient
  ) { }

  findAll(): Observable<CustomerDto[]> {
    return this.http.get<CustomerDto[]>(
      this.customerUrl
    );
  }

  registerCustomer(customer: CustomerRegistrationRequest): Observable<void> {
    return this.http.post<void>(
      this.customerUrl,
      customer
    );
  }

  deleteCustomer(id: number | undefined): Observable<void> {
    return this.http.delete<void>(`${this.customerUrl}/${id}`)
  }

  updateCustomer(id: number | undefined, customer: CustomerUpdateRequest): Observable<void> {
    return this.http.put<void>(
      `${this.customerUrl}/${id}`,
      customer
    );
  }
}
