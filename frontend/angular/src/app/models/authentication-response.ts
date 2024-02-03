import {CustomerDto} from "./customer-dto";

export interface AuthenticationResponse {

  jwtToken?: string;
  customerDTO: CustomerDto;
}
