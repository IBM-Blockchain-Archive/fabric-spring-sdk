package main

import (
	"encoding/json"
	"fmt"
	"strconv"

	"github.com/hyperledger/fabric/core/chaincode/shim"
	"github.com/hyperledger/fabric/protos/peer"
)

const keyBudget = "$$"
const itemPrefix = "i#"

// ShopContract implements a simple chaincode to manage a shop
type ShopContract struct {
}

// Item structure
type Item struct {
	Amount    int `json:"amount"`
	BuyPrice  int `json:"buyprice"`
	SellPrice int `json:"sellprice"`
}

// Init is called during chaincode instantiation
func (t *ShopContract) Init(stub shim.ChaincodeStubInterface) peer.Response {
	args := stub.GetStringArgs()
	if len(args) != 1 {
		return shim.Error("Incorrect number of arguments. Should be 1")
	}

	// Checking the validity of initial budget
	budget, err := strconv.Atoi(args[0])
	if err != nil || budget < 0 {
		return shim.Error("Illegal value for initial budget")
	}

	// Set the budget to be the argument
	err = stub.PutState(keyBudget, []byte(args[0]))
	if err != nil {
		return shim.Error(fmt.Sprintf("Failed to create shop"))
	}
	return shim.Success(nil)
}

// Invoke is called per transaction on the chaincode
func (t *ShopContract) Invoke(stub shim.ChaincodeStubInterface) peer.Response {
	fn, args := stub.GetFunctionAndParameters()

	switch fn {
	case "report":
		return report(stub, args)
	case "add":
		return add(stub, args)
	case "buy":
		return buy(stub, args)
	case "sell":
		return sell(stub, args)
	default:
		return shim.Error("Illegal function name")
	}
}

// Report returns the current budget
func report(stub shim.ChaincodeStubInterface, args []string) peer.Response {
	if len(args) != 0 {
		return shim.Error("Incorrect number of arguments. Expecting 0")
	}
	budget, err := stub.GetState(keyBudget)
	if err != nil {
		return shim.Error("Failed to report")
	}
	return shim.Success(budget)
}

// Add creates a new item type with a given buy and sell prices
func add(stub shim.ChaincodeStubInterface, args []string) peer.Response {
	if len(args) != 3 {
		return shim.Error("Incorrect number of arguments. Expecting 3")
	}

	buyPrice, _ := strconv.Atoi(args[1])
	sellPrice, _ := strconv.Atoi(args[2])
	var item = Item{Amount: 0, BuyPrice: buyPrice, SellPrice: sellPrice}

	itemAsBytes, _ := json.Marshal(item)
	err := stub.PutState(itemPrefix+args[0], itemAsBytes)
	if err != nil {
		return shim.Error(fmt.Sprintf("Failed to add item"))
	}

	return shim.Success(nil)
}

// Buy uses the budget to purchase an amount of an argument item
func buy(stub shim.ChaincodeStubInterface, args []string) peer.Response {
	if len(args) != 2 {
		return shim.Error("Incorrect number of arguments. Expecting 2")
	}

	buyAmount, _ := strconv.Atoi(args[1])
	if buyAmount < 0 {
		return shim.Error("Invalid amount, must be positive")
	}

	itemAsBytes, err := stub.GetState(itemPrefix + args[0])
	if err != nil {
		return shim.Error("Item doesn't exist or is invalid")
	}
	item := Item{}
	json.Unmarshal(itemAsBytes, &item)

	budgetAsBytes, err := stub.GetState(keyBudget)
	if err != nil {
		return shim.Error("Failed to access the budget")
	}
	budget, _ := strconv.Atoi(string(budgetAsBytes))
	if budget < buyAmount*item.BuyPrice {
		return shim.Error("Insufficient budget")
	}
	budgetAsBytes = []byte(strconv.Itoa(budget - buyAmount*item.BuyPrice))
	err = stub.PutState(keyBudget, budgetAsBytes)
	if err != nil {
		return shim.Error("Failed to update budget")
	}

	item.Amount += buyAmount
	itemAsBytes, _ = json.Marshal(item)
	err = stub.PutState(itemPrefix+args[0], itemAsBytes)
	if err != nil {
		return shim.Error("Failed to update item")
	}

	return shim.Success(nil)
}

// Sell uses an amount of an argument item to update the budget
func sell(stub shim.ChaincodeStubInterface, args []string) peer.Response {
	if len(args) != 2 {
		return shim.Error("Incorrect number of arguments. Expecting 3")
	}

	sellAmount, _ := strconv.Atoi(args[1])
	if sellAmount < 0 {
		return shim.Error("Invalid amount, must be positive")
	}

	itemAsBytes, err := stub.GetState(itemPrefix + args[0])
	if err != nil {
		return shim.Error("Item doesn't exist or is invalid")
	}
	item := Item{}
	json.Unmarshal(itemAsBytes, &item)

	if sellAmount > item.Amount {
		return shim.Error("Insufficient stock")
	}

	budgetAsBytes, err := stub.GetState(keyBudget)
	if err != nil {
		return shim.Error("Failed to access the budget")
	}
	budget, _ := strconv.Atoi(string(budgetAsBytes))
	budgetAsBytes = []byte(strconv.Itoa(budget + sellAmount*item.SellPrice))
	err = stub.PutState(keyBudget, budgetAsBytes)
	if err != nil {
		return shim.Error("Failed to update budget")
	}

	item.Amount -= sellAmount
	itemAsBytes, _ = json.Marshal(item)
	err = stub.PutState(itemPrefix+args[0], itemAsBytes)
	if err != nil {
		return shim.Error("Failed to update item")
	}

	return shim.Success(nil)
}

// main function starts up the chaincode in the container during instantiate
func main() {
	if err := shim.Start(new(ShopContract)); err != nil {
		fmt.Printf("Error starting ShopContract chaincode: %s", err)
	}
}
