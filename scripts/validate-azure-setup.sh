#!/bin/bash
echo "🔍 Validating Azure Setup for Library Management System"
echo "======================================================"

# Set variables
RESOURCE_GROUP="library-rg"
WEBAPP_EAST="library-app-eastus"
WEBAPP_CENTRAL="library-app-centralus"

echo "📋 Checking Resource Group: $RESOURCE_GROUP"
if az group show --name $RESOURCEGROUP --query "name" -o tsv; then
    echo "✅ Resource Group exists"
else
    echo "❌ Resource Group not found!"
    exit 1
fi

echo ""
echo "📋 Checking Web Apps..."
echo "East US Web App: $WEBAPP_EAST"
if az webapp show --name $WEBAPP_EAST --resource-group $RESOURCE_GROUP --query "name" -o tsv; then
    echo "✅ East US Web App exists"
else
    echo "❌ East US Web App not found!"
    exit 1
fi

echo "Central US Web App: $WEBAPP_CENTRAL"
if az webapp show --name $WEBAPP_CENTRAL --resource-group $RESOURCE_GROUP --query "name" -o tsv; then
    echo "✅ Central US Web App exists"
else
    echo "❌ Central US Web App not found!"
    exit 1
fi

echo ""
echo "📋 Checking App Service Plans..."
ASP_EAST=$(az webapp show --name $WEBAPP_EAST --resource-group $RESOURCE_GROUP --query "serverFarmId" -o tsv | cut -d'/' -f9)
ASP_CENTRAL=$(az webapp show --name $WEBAPP_CENTRAL --resource-group $RESOURCE_GROUP --query "serverFarmId" -o tsv | cut -d'/' -f9)

echo "East US App Service Plan: $ASP_EAST"
if az appservice plan show --name $ASP_EAST --resource-group $RESOURCE_GROUP --query "name" -o tsv; then
    echo "✅ East US App Service Plan exists"
else
    echo "❌ East US App Service Plan not found!"
fi

echo "Central US App Service Plan: $ASP_CENTRAL"
if az appservice plan show --name $ASP_CENTRAL --resource-group $RESOURCE_GROUP --query "name" -o tsv; then
    echo "✅ Central US App Service Plan exists"
else
    echo "❌ Central US App Service Plan not found!"
fi

echo ""
echo "🎉 Azure setup validation completed successfully!"
echo "👉 Next steps: Run GitHub Actions workflow to deploy the application"