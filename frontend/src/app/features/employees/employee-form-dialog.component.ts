import { Component, inject } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { MAT_DIALOG_DATA, MatDialogModule, MatDialogRef } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatButtonModule } from '@angular/material/button';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { MatNativeDateModule } from '@angular/material/core';
import { Employee, EmployeeRequest, EmployeeStatus } from '../../core/employees/employee.models';

export interface EmployeeFormDialogData {
  employee?: Employee;
}

@Component({
  selector: 'app-employee-form-dialog',
  standalone: true,
  imports: [
    ReactiveFormsModule,
    MatDialogModule,
    MatFormFieldModule,
    MatInputModule,
    MatSelectModule,
    MatButtonModule,
    MatDatepickerModule,
    MatNativeDateModule
  ],
  templateUrl: './employee-form-dialog.component.html',
  styleUrl: './employee-form-dialog.component.scss'
})
export class EmployeeFormDialogComponent {
  private readonly fb = inject(FormBuilder);
  private readonly dialogRef = inject(MatDialogRef<EmployeeFormDialogComponent, EmployeeRequest | undefined>);
  readonly data = inject<EmployeeFormDialogData>(MAT_DIALOG_DATA);

  readonly statuses: EmployeeStatus[] = ['ACTIVE', 'INACTIVE', 'ON_LEAVE'];
  readonly isEdit = !!this.data.employee;

  readonly form = this.fb.nonNullable.group({
    employeeCode: [this.data.employee?.employeeCode ?? '', [Validators.required, Validators.maxLength(20)]],
    firstName: [this.data.employee?.firstName ?? '', [Validators.required, Validators.maxLength(50)]],
    lastName: [this.data.employee?.lastName ?? '', [Validators.required, Validators.maxLength(50)]],
    email: [this.data.employee?.email ?? '', [Validators.required, Validators.email, Validators.maxLength(100)]],
    phone: [this.data.employee?.phone ?? ''],
    department: [this.data.employee?.department ?? '', [Validators.required, Validators.maxLength(50)]],
    status: [this.data.employee?.status ?? ('ACTIVE' as EmployeeStatus), Validators.required],
    hireDate: [
      this.data.employee ? new Date(this.data.employee.hireDate) : new Date(),
      Validators.required
    ]
  });

  save(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    const raw = this.form.getRawValue();
    const hireDate = raw.hireDate instanceof Date
      ? raw.hireDate.toISOString().slice(0, 10)
      : String(raw.hireDate);

    this.dialogRef.close({
      employeeCode: raw.employeeCode.trim(),
      firstName: raw.firstName.trim(),
      lastName: raw.lastName.trim(),
      email: raw.email.trim(),
      phone: raw.phone?.trim() || null,
      department: raw.department.trim(),
      status: raw.status,
      hireDate
    });
  }

  cancel(): void {
    this.dialogRef.close(undefined);
  }
}
