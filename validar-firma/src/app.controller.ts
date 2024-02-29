import { Body, Controller, Get, Post } from '@nestjs/common';
import { AppService } from './app.service';
import * as fs from 'fs';
import { Application, Parse, SignedXml } from 'xadesjs';
import { Crypto } from '@peculiar/webcrypto';

Application.setEngine('NodeJS', new Crypto());

@Controller()
export class AppController {
  constructor(private readonly appService: AppService) {}

  @Get()
  getHello(): string {
    return this.appService.getHello();
  }

  @Post('v1/validar')
  async validateXml(@Body() data: { xml: string }): Promise<Boolean> {
    const xmlPath = data.xml;
    const xmlString = fs.readFileSync(xmlPath, 'utf-8');
    const signedDocument = Parse(xmlString);
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
