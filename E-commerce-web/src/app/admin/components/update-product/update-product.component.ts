import { Component } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { AdminService } from '../../service/admin.service';
import { MatSnackBar } from '@angular/material/snack-bar';
import { HttpResponse } from '@angular/common/http';

@Component({
  selector: 'app-update-product',
  templateUrl: './update-product.component.html',
  styleUrl: './update-product.component.scss'
})
export class UpdateProductComponent {
  productForm: FormGroup;
  listOfCategories: any = [];
  selectedFile: File | null;
  imagepreview: string | ArrayBuffer | null;
  productId = this.activatedRoute.snapshot.params['productId'];
  existingImage : string | null = null;

  imgChanged = false;

  constructor(
    private fb: FormBuilder,
    private router: Router,
    private snackbar: MatSnackBar,
    private adminService: AdminService,
    private activatedRoute: ActivatedRoute
  ) { }

  onFileSelected(event: any) {
    this.selectedFile = event.target.files[0];
    this.previewImage();
    this.imgChanged = true;
    this.existingImage = null;
  }

  previewImage() {
    const reader = new FileReader();
    reader.onload = () => {
      this.imagepreview = reader.result;
    }
    reader.readAsDataURL(this.selectedFile);
  }

  ngOnInit(): void {
    this.productForm = this.fb.group({
      categoryId: [null, [Validators.required]],
      name: [null, [Validators.required]],
      price: [null, [Validators.required]],
      description: [null, [Validators.required]],
    });
    this.getAllCategories();
    this.getProductById();
  }

  getAllCategories() {
    this.adminService.getAllCategories().subscribe(res => {
      this.listOfCategories = res;
    })
  }

  getProductById(){
    this.adminService.getProductById(this.productId).subscribe(res=>{
      this.productForm.patchValue(res);
      this.existingImage = 'data:image/jpeg;base64,' + res.byteImg;
    })
  }
}
