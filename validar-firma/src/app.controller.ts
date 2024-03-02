import { Body, Controller, Get, Post, UploadedFile, UseInterceptors } from '@nestjs/common';
import { AppService } from './app.service';
import * as fs from 'fs';
import { Application, Parse, SignedXml } from 'xadesjs';
import { Crypto } from '@peculiar/webcrypto';
import { FileInterceptor } from '@nestjs/platform-express';

Application.setEngine('NodeJS', new Crypto());

@Controller()
export class AppController {
  constructor(private readonly appService: AppService) {}

  @Get()
  getHello(): string {
    return this.appService.getHello();
  }

  @Post('v1/validar')
  @UseInterceptors(FileInterceptor('file'))
  async validateXml(@UploadedFile() xml:  Express.Multer.File ): Promise<Boolean> {
    //const xmlPath = data.xml;
    //const xmlString = fs.readFileSync(xmlPath, 'utf-8');
    const signedDocument = Parse(xml.buffer.toString());
    const xmlSignature = signedDocument.getElementsByTagNameNS(
      'http://www.w3.org/2000/09/xmldsig#',
      'Signature',
    );

    const signedXml = new SignedXml(signedDocument);
    signedXml.LoadXml(xmlSignature[0]);

    try {
      const response = await signedXml.Verify();
      console.log((response ? 'Valid' : 'Invalid') + ' signature');
      return response;
    } catch (error) {
      console.error(error);
      return false;
    }
  }
}
