package com.hakum.customer.demo

import com.hakum.customer.data.model.*

object DemoCatalog {
    val branches=linkedMapOf("bacoor" to "Bacoor · RFC Molino","batangas" to "Batangas · PNP Batangas")
    // Fictional prices for local demonstration, points from rewardsCatalog.js.
    val services=listOf(
        Service("wash","Premium car wash","wash",35000L,1,description="Local demo quote · source catalog award."),
        Service("express","Express","detailing",150000L,2,description="Local demo quote · source catalog award."),
        Service("basic","Basic","detailing",250000L,3,description="Local demo quote · source catalog award."),
        Service("deluxe","Deluxe","detailing",400000L,4,description="Local demo quote · source catalog award."),
        Service("paint-maintenance","Paint Maintenance","detailing",500000L,5,description="Local demo quote · source catalog award."),
        Service("paint-refresh","Paint Refresh","detailing",500000L,5,description="Local demo quote · source catalog award."),
        Service("glass-cleaning","Glass cleaning","detailing",300000L,3,description="Local demo quote · source catalog award."),
        Service("headlight-restore","Headlight Restore","detailing",300000L,3,description="Local demo quote · source catalog award."),
        Service("black-trims-restore","Black trims restore","detailing",300000L,3,description="Local demo quote · source catalog award."),
        Service("glass-detailing","Glass detailing","detailing",500000L,5,description="Local demo quote · source catalog award."),
        Service("glass-coating","Glass coating","detailing",500000L,5,description="Local demo quote · source catalog award."),
        Service("ceramic","Classic ceramic","detailing",1800000L,15,description="Local demo quote · source catalog award."),
        Service("premium-ceramic","Premium ceramic","detailing",2000000L,20,description="Local demo quote · source catalog award."),
        Service("platinum-ceramic","Platinum ceramic","detailing",2500000L,25,description="Local demo quote · source catalog award."),
        Service("tint","Nano Ceramic Tint","detailing",800000L,10,description="Local demo quote · source catalog award."),
        Service("nano-ceramic-pro-tint","Nano Ceramic Pro Tint","detailing",1500000L,15,description="Local demo quote · source catalog award."),
        Service("nano-ceramic-pro-black-series-tint","Nano Ceramic Pro Black Series Tint","detailing",2000000L,20,description="Local demo quote · source catalog award."),
        Service("interior-detailing","Interior Detailing","detailing",1000000L,10,description="Local demo quote · source catalog award."),
        Service("interior-deep-cleaning","Interior Deep Cleaning","detailing",500000L,5,description="Local demo quote · source catalog award."),
        Service("ppf","High Impact Partial PPF","ppf",4800000L,30,description="Local demo quote · source catalog award."),
        Service("basic-ppf","Basic PPF","ppf",4000000L,40,description="Local demo quote · source catalog award."),
        Service("ultimate-ppf","Ultimate PPF","ppf",5000000L,50,description="Local demo quote · source catalog award."),
        Service("platinum-ppf","Platinum PPF","ppf",6000000L,60,description="Local demo quote · source catalog award."),
    )
    val rewards=listOf(
        Reward("stamp-10","Free wash",threshold=10),Reward("stamp-15","Premium detail",threshold=15),
        Reward("visit-6","Free car freshener",threshold=6,kind="visit"),
        Reward("visit-12","Free coffee",threshold=12,kind="visit"),
        Reward("visit-18","Free premium car wash",threshold=18,kind="visit"),
        Reward("visit-24","Free back-to-zero add-on",threshold=24,kind="visit"),
        Reward("visit-30","15% off tint, ceramic coating, or PPF (choose one)",threshold=30,kind="visit"),
        Reward("merch-shirt","Hakum Shirt",pointsCost=50,kind="points"),
        Reward("merch-mug","Clear Pro mug",pointsCost=30,kind="points",stockBatangas=0),
        Reward("clear-pro-yellow-bag","Clear Pro Yellow Bag",pointsCost=30,kind="points"),
        Reward("merch-coffee","Clear Pro Coffee Kit",pointsCost=60,kind="points"),
        Reward("merch-key","Hakum Key Chain",pointsCost=20,kind="points"),
        Reward("merch-care","Hakum Car Care Kit",pointsCost=50,kind="points"),
        Reward("fusso-coat-spray","Fusso coat spray",pointsCost=50,kind="points"),
        Reward("luxury-gloss-spray","Luxury gloss spray",pointsCost=50,kind="points"),
        Reward("merch-scent","Hakum car scent",pointsCost=30,kind="points"),
        Reward("air-spencer","Air Spencer",pointsCost=30,kind="points")
    )
}
