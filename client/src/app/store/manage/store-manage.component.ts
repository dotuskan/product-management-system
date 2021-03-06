import {Component, OnInit} from "@angular/core";
import {Store} from "../../model/store";
import {CommonService} from "../../common/common.service";
import {ActivatedRoute} from "@angular/router";
import {api} from "../../constants/api";
import {NotificationService} from "../../notification/notification.service";
import {StoreService} from "../store.service";
import {Stock} from "../../model/stock";

@Component({
    selector: 'store-manage-component',
    templateUrl: 'store-manage.component.html'
})
export class StoreManageComponent implements OnInit {
    store: Store;
    stockList: Stock[];
    availableStocks: Stock[];

    constructor(private commonService: CommonService,
                private route: ActivatedRoute,
                private notificationService: NotificationService,
                private storeService: StoreService) {
    }

    ngOnInit(): void {
        this.loadData();
    }

    private loadData(): void {
        this.commonService.loadByIdUnauthorized(api.STORE, this.route.snapshot.params['id'])
            .subscribe(
                store => {
                    this.store = store;
                    this.storeService.getStocks(store.id)
                        .subscribe(
                            stockList => {
                                this.stockList = stockList;
                                this.commonService.loadAll(api.STOCK)
                                    .subscribe(availableStocks => this.availableStocks = this.parseStocks(stockList, availableStocks));
                            },
                            err => this.logError(err));
                },
                err => this.logError(err));
    }

    public onAddStock(stockId: string): void {
        this.storeService.addStock(this.store.id, stockId).subscribe(
            () => {
                this.notificationService.success("Stock was successfully added");
                this.loadData();
            },
            ((error: Error) => this.logError(error)));
    }

    public onRemoveStock(stockId: string): void {
        this.storeService.removeStock(this.store.id, stockId).subscribe(
            () => {
                this.notificationService.success("Stock was successfully removed");
                this.loadData();
            },
            ((error: Error) => this.logError(error)));
    }

    private parseStocks(currentStocks: Stock[], availableStocks: Stock[]): Stock[] {
        const currentStocksIds: string[] = currentStocks.map(value => value.id);
        return availableStocks.filter(available => currentStocksIds.indexOf(available.id) == -1);
    }

    private logError(error: Error): void {
        this.notificationService.error(error.message ? error.message : error.toString());
    }
}